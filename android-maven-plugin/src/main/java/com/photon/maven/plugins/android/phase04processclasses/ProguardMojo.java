/**
 * Android Maven Plugin - android-maven-plugin
 *
 * Copyright (C) 1999-2014 Photon Infotech Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.photon.maven.plugins.android.phase04processclasses;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

import com.photon.maven.plugins.android.AbstractAndroidMojo;
import com.photon.maven.plugins.android.CommandExecutor;
import com.photon.maven.plugins.android.ExecutionException;
import com.photon.maven.plugins.android.common.AndroidExtension;
import com.photon.maven.plugins.android.common.EclipseNativeHelper;
import com.photon.maven.plugins.android.common.NativeHelper;
import com.photon.maven.plugins.android.config.ConfigHandler;
import com.photon.maven.plugins.android.config.ConfigPojo;
import com.photon.maven.plugins.android.config.PullParameter;
import com.photon.maven.plugins.android.configuration.Proguard;

/**
 * Processes both application and dependency classes using the ProGuard byte code obfuscator,
 * minimzer, and optimizer. For more information, see https://proguard.sourceforge.net.
 *
 * @goal proguard
 * @phase process-classes
 * @requiresDependencyResolution compile
 */
public class ProguardMojo extends AbstractAndroidMojo
{

    /**
     * <p>
     * ProGuard configuration. ProGuard is disabled by default. Set the skip parameter to false to activate proguard.
     * A complete configuartion can include any of the following:
     * </p>
     * <p/>
     * <pre>
     * &lt;proguard&gt;
     *    &lt;skip&gt;true|false&lt;/skip&gt;
     *    &lt;config&gt;proguard.cfg&lt;/config&gt;
     *    &lt;configs&gt;
     *      &lt;config&gt;${env.ANDROID_HOME}/tools/proguard/proguard-android.txt&lt;/config&gt;
     *    &lt;/configs&gt;
     *    &lt;proguardJarPath&gt;someAbsolutePathToProguardJar&lt;/proguardJarPath&gt;
     *    &lt;filterMavenDescriptor&gt;true|false&lt;/filterMavenDescriptor&gt;
     *    &lt;filterManifest&gt;true|false&lt;/filterManifest&gt;
     *    &lt;jvmArguments&gt;
     *     &lt;jvmArgument&gt;-Xms256m&lt;/jvmArgument&gt;
     *     &lt;jvmArgument&gt;-Xmx512m&lt;/jvmArgument&gt;
     *   &lt;/jvmArguments&gt;
     * &lt;/proguard&gt;
     * </pre>
     * <p>
     * A good practice is to create a release profile in your POM, in which you enable ProGuard.
     * ProGuard should be disabled for development builds, since it obfuscates class and field
     * names, and it may interfere with test projects that rely on your application classes.
     * All parameters can be overridden in profiles or the the proguard* properties. Default values apply and are
     * documented with these properties.
     * </p>
     *
     * @parameter
     */
    @ConfigPojo
    protected Proguard proguard;

    /**
     * Whether ProGuard is enabled or not. Defaults to true.
     *
     * @parameter expression="${android.proguard.skip}"
     * @optional
     */
    public Boolean proguardSkip;

    @PullParameter( defaultValue = "true" )
    private Boolean parsedSkip;

    /**
     * Path to the ProGuard configuration file (relative to project root). Defaults to "source/proguard.cfg"
     *
     * @parameter expression="${android.proguard.config}"
     * @optional
     */
    private String proguardConfig;

    @PullParameter( defaultValue = "proguard.cfg" )
    private String parsedConfig;

    /**
     * Additional ProGuard configuration files (relative to project root).
     *
     * @parameter expression="${android.proguard.configs}"
     * @optional
     */
    private String[] proguardConfigs;

    @PullParameter( defaultValueGetterMethod = "getDefaultProguardConfigs" )
    private String[] parsedConfigs;

    /**
     * Path to the proguard jar and therefore version of proguard to be used. By default this will load the jar from
     * the Android SDK install. Overriding it with an absolute path allows you to use a newer or custom proguard
     * version..
     * <p/>
     * You can also reference an external Proguard version as a plugin dependency like this:
     * <pre>
     * &lt;plugin&gt;
     *   &lt;groupId&gt;com.jayway.maven.plugins.android.generation2&lt;/groupId&gt;
     *   &lt;artifactId&gt;android-maven-plugin&lt;/artifactId&gt;
     *     &lt;dependencies&gt;
     *       &lt;dependency&gt;
     *         &lt;groupId&gt;net.sf.proguard&lt;/groupId&gt;
     *         &lt;artifactId&gt;proguard-base&lt;/artifactId&gt;
     *         &lt;version&gt;4.7&lt;/version&gt;
     *       &lt;/dependency&gt;
     *     &lt;/dependencies&gt;
     * </pre>
     * <p/>
     * which will download and use Proguard 4.7 as deployed to the Central Repository.
     *
     * @parameter expression="${android.proguard.proguardJarPath}
     * @optional
     */
    private String proguardProguardJarPath;

    @PullParameter( defaultValueGetterMethod = "getProguardJarPath" )
    private String parsedProguardJarPath;
    
    /**
     * Path relative to the project's build directory (target) where proguard puts folowing files:
     * <p/>
     * <ul>
     *   <li>dump.txt</li>
     *   <li>seeds.txt</li>
     *   <li>usage.txt</li>
     *   <li>mapping.txt</li>
     * </ul>
     * <p/>
     * You can define the directory like this:
     * <pre>
     * &lt;proguard&gt;
     *   &lt;skip&gt;false&lt;/skip&gt;
     *   &lt;config&gt;proguard.cfg&lt;/config&gt;
     *   &lt;outputDirectory&gt;my_proguard&lt;/outputDirectory&gt;
     * &lt;/proguard&gt; 
     * </pre>
     * <p/>
     * Output directory is defined relatively so it could be also outside of the target directory.
     * <p/>
     *
     * @parameter expression="${android.proguard.outputDirectory}"  default-value="proguard"
     * @optional
     */
    private String outputDirectory;

    @PullParameter( defaultValue = "proguard" )
    private String parsedOutputDirectory;
    

    /**
     * Extra JVM Arguments. Using these you can e.g. increase memory for the jvm running the build.
     * Defaults to "-Xmx512M".
     *
     * @parameter expression="${android.proguard.jvmArguments}"
     * @optional
     */
    private String[] proguardJvmArguments;

    @PullParameter( defaultValueGetterMethod = "getDefaultJvmArguments" )
    private String[] parsedJvmArguments;

    /**
     * If set to true will add a filter to remove META-INF/maven/* files. Defaults to false.
     *
     * @parameter expression="${android.proguard.filterMavenDescriptor}"
     * @optional
     */
    private Boolean proguardFilterMavenDescriptor;

    @PullParameter( defaultValue = "true" )
    private Boolean parsedFilterMavenDescriptor;

    /**
     * If set to true will add a filter to remove META-INF/MANIFEST.MF files.  Defaults to false.
     *
     * @parameter expression="${android.proguard.filterManifest}"
     * @optional
     */
    private Boolean proguardFilterManifest;

    @PullParameter( defaultValue = "true" )
    private Boolean parsedFilterManifest;
    
    /**
     * If set to true JDK jars will be included as library jars and corresponding filters
     * will be applied to android.jar.  Defaults to true.
     * @parameter expression="${android.proguard.includeJdkLibs}"
     */
    private Boolean includeJdkLibs;
    
    /**
  	 * @parameter expression="${project.basedir}" required="true"
  	 * @readonly
  	 */
  	protected File baseDir;
    
    @PullParameter( defaultValue = "true" )
    private Boolean parsedIncludeJdkLibs;

    /**
     * The plugin dependencies.
     *
     * @parameter expression="${plugin.artifacts}"
     * @required
     * @readonly
     */
    protected List<Artifact> pluginDependencies;

    public static final String PROGUARD_OBFUSCATED_JAR = "proguard-obfuscated.jar";

    private static final Collection<String> ANDROID_LIBRARY_EXCLUDED_FILTER = Arrays
            .asList( "org/xml/**", "org/w3c/**", "java/**", "javax/**" );

    private static final Collection<String> MAVEN_DESCRIPTOR = Arrays.asList( "META-INF/maven/**" );
    private static final Collection<String> META_INF_MANIFEST = Arrays.asList( "META-INF/MANIFEST.MF" );

    private Collection<String> globalInJarExcludes = new HashSet<String>();

    private List<Artifact> artifactBlacklist = new LinkedList<Artifact>();
    private List<Artifact> artifactsToShift = new LinkedList<Artifact>();

    private List<ProGuardInput> inJars = new LinkedList<ProguardMojo.ProGuardInput>();
    private List<ProGuardInput> libraryJars = new LinkedList<ProguardMojo.ProGuardInput>();
    private static boolean proGuardSkipProp;

    private static class ProGuardInput
    {

        private String path;
        private Collection<String> excludedFilter;

        public ProGuardInput( String path, Collection<String> excludedFilter )
        {
            this.path = path;
            this.excludedFilter = excludedFilter;
        }

        public String toCommandLine()
        {
            if ( excludedFilter != null && ! excludedFilter.isEmpty() )
            {
                StringBuilder sb = new StringBuilder( path );
                sb.append( '(' );
                for ( Iterator<String> it = excludedFilter.iterator(); it.hasNext(); )
                {
                    sb.append( '!' ).append( it.next() );
                    if ( it.hasNext() )
                    {
                        sb.append( ',' );
                    }
                }
                sb.append( ')' );
                return sb.toString();
            }
            else
            {
                return "\'" + path + "\'";
            }
        }
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {    
        if(proguardSkip!=null){
            setProGuardSkipProp(proguardSkip);
        }
       ConfigHandler configHandler = new ConfigHandler( this );
        configHandler.parseConfiguration();

        if ( ! parsedSkip && ! baseDir.getPath().endsWith("unit"))
        {
            executeProguard();
        }
    }

    private void executeProguard() throws MojoExecutionException
    {
        final File proguardDir = new File( project.getBuild().getDirectory(), parsedOutputDirectory );
          
        if ( ! proguardDir.exists() && ! proguardDir.mkdir() )
        {
            throw new MojoExecutionException( "Cannot create proguard output directory" );
        }
        else
        {
            if ( proguardDir.exists() && ! proguardDir.isDirectory() )
            {
                throw new MojoExecutionException( "Non-directory exists at " + proguardDir.getAbsolutePath() );
            }
        }

        CommandExecutor executor = CommandExecutor.Factory.createDefaultCommmandExecutor();
        executor.setLogger( this.getLog() );
        List<String> commands = new ArrayList<String>();

        collectJvmArguments( commands );

        commands.add( "-jar" );
        commands.add( parsedProguardJarPath );

        commands.add( "@" + parsedConfig );

        for ( String config : parsedConfigs )
        {
            commands.add( "@" + config );
        }

        if ( proguardFile != null )
        {
            commands.add( "@" + proguardFile.getAbsolutePath() );
        }

        collectInputFiles( commands );

        commands.add( "-outjars" );
        commands.add( "'" + project.getBuild().getDirectory() + File.separator + PROGUARD_OBFUSCATED_JAR + "'" );

        commands.add( "-dump" );
        commands.add( "'" + proguardDir + File.separator + "dump.txt'" );
        commands.add( "-printseeds" );
        commands.add( "'" + proguardDir + File.separator + "seeds.txt'" );
        commands.add( "-printusage" );
        commands.add( "'" + proguardDir + File.separator + "usage.txt'" );
        commands.add( "-printmapping" );
        commands.add( "'" + proguardDir + File.separator + "mapping.txt'" );

        final String javaExecutable = getJavaExecutable().getAbsolutePath();
        getLog().info( javaExecutable + " " + commands.toString() );
        try
        {
            executor.executeCommand( javaExecutable, commands, project.getBasedir(), false );
        }
        catch ( ExecutionException e )
        {
            throw new MojoExecutionException( "", e );
        }
    }

    /**
     * Convert the jvm arguments in parsedJvmArguments as populated by the config in format as needed by the java
     * command. Also preserve backwards compatibility in terms of dashes required or not..
     *
     * @param commands
     */
    private void collectJvmArguments( List<String> commands )
    {
        if ( parsedJvmArguments != null )
        {
            for ( String jvmArgument : parsedJvmArguments )
            {
                // preserve backward compatibility allowing argument with or without dash (e.g.
                // Xmx512m as well as -Xmx512m should work) (see
                // http://code.google.com/p/maven-android-plugin/issues/detail?id=153)
                if ( ! jvmArgument.startsWith( "-" ) )
                {
                    jvmArgument = "-" + jvmArgument;
                }
                commands.add( jvmArgument );
            }
        }
    }

    private void collectInputFiles( List<String> commands )
    {
        // commons-logging breaks everything horribly, so we skip it from the program
        // dependencies and declare it to be a library dependency instead
        skipArtifact( "commons-logging", "commons-logging", true );

        collectProgramInputFiles();
        for ( ProGuardInput injar : inJars )
        {
            // don't add android packaging files, these are not input to proguard
            if ( ! AndroidExtension.isAndroidPackaging( FileUtils.extension( injar.path ) ) )
            {
                commands.add( "-injars" );
                commands.add( injar.toCommandLine() );
            }
        }

        collectLibraryInputFiles();
        for ( ProGuardInput libraryjar : libraryJars )
        {
            commands.add( "-libraryjars" );
            commands.add( libraryjar.toCommandLine() );
        }
    }

    /**
     * Figure out the full path to the current java executable.
     *
     * @return the full path to the current java executable.
     */
    private static File getJavaExecutable()
    {
        final String javaHome = System.getProperty( "java.home" );
        final String slash = File.separator;
        return new File( javaHome + slash + "bin" + slash + "java" );
    }

    private void skipArtifact( String groupId, String artifactId, boolean shiftToLibraries )
    {
    	if (container.hasComponent("org.sonatype.aether.RepositorySystem")) {
    		artifactBlacklist.add( NativeHelper.toArtifact( groupId, artifactId ) );
            if ( shiftToLibraries )
            {
                artifactsToShift
                        .add( NativeHelper.toArtifact(groupId, artifactId) );
            }
    	} else if (container.hasComponent("org.eclipse.aether.RepositorySystem")) {
    		artifactBlacklist.add( EclipseNativeHelper.toArtifact( groupId, artifactId ) );
            if ( shiftToLibraries )
            {
                artifactsToShift
                        .add( EclipseNativeHelper.toArtifact(groupId, artifactId) );
            }
    	}
        
    }

    private boolean isBlacklistedArtifact( Artifact artifact )
    {
        for ( Artifact artifactToSkip : artifactBlacklist )
        {
            if ( artifactToSkip.getGroupId().equals( artifact.getGroupId() ) && artifactToSkip.getArtifactId()
                    .equals( artifact.getArtifactId() ) )
            {
                return true;
            }
        }
        return false;
    }

    private boolean isShiftedArtifact( Artifact artifact )
    {
        for ( Artifact artifactToShift : artifactsToShift )
        {
            if ( artifactToShift.getGroupId().equals( artifact.getGroupId() ) && artifactToShift.getArtifactId()
                    .equals( artifact.getArtifactId() ) )
            {
                return true;
            }
        }
        return false;
    }

    private void collectProgramInputFiles()
    {
        if ( parsedFilterManifest )
        {
            globalInJarExcludes.addAll( META_INF_MANIFEST );
        }
        if ( parsedFilterMavenDescriptor )
        {
            globalInJarExcludes.addAll( MAVEN_DESCRIPTOR );
        }

        // we first add the application's own class files
        addInJar( project.getBuild().getOutputDirectory() );

        // we then add all its dependencies (incl. transitive ones), unless they're blacklisted
        for ( Artifact artifact : getAllRelevantDependencyArtifacts() )
        {
            if ( isBlacklistedArtifact( artifact ) )
            {
                continue;
            }
            addInJar( artifact.getFile().getAbsolutePath(), globalInJarExcludes );
        }
    }

    private void addInJar( String path, Collection<String> filterExpression )
    {
        inJars.add( new ProGuardInput( path, filterExpression ) );
    }

    private void addInJar( String path )
    {
        addInJar( path, null );
    }

    private void addLibraryJar( String path, Collection<String> filterExpression )
    {
        libraryJars.add( new ProGuardInput( path, filterExpression ) );
    }

    private void addLibraryJar( String path )
    {
        addLibraryJar( path, null );
    }

    private void collectLibraryInputFiles()
    {
        if ( parsedIncludeJdkLibs )
        {
            final String slash = File.separator;
            // we have to add the Java framework classes to the library JARs, since they are not
            // distributed with the JAR on Central, and since we'll strip them out of the android.jar
            // that is shipped with the SDK (since that is not a complete Java distribution)
            String javaHome = System.getProperty( "java.home" );
            String jdkLibsPath = null;
            if ( isMacOSXJDKbyApple( javaHome ) )
            {
                // MacOS X uses different naming conventions for JDK installations
                jdkLibsPath = appleJDKLibsPath( javaHome );
                addLibraryJar( jdkLibsPath + "/classes.jar" );
            }
            else
            {
                jdkLibsPath = javaHome + slash + "lib";
                addLibraryJar( jdkLibsPath + slash + "rt.jar" );
            }
            // we also need to add the JAR containing e.g. javax.servlet
            addLibraryJar( jdkLibsPath + slash + "jsse.jar" );
            // and the javax.crypto stuff
            addLibraryJar( jdkLibsPath + slash + "jce.jar" );
        }

        // we treat any dependencies with provided scope as library JARs
        for ( Artifact artifact : project.getArtifacts() )
        {
            if ( artifact.getScope().equals( "provided" ) )
            {
                if ( artifact.getArtifactId().equals( "android" ) && parsedIncludeJdkLibs )
                {
                    addLibraryJar( artifact.getFile().getAbsolutePath(), ANDROID_LIBRARY_EXCLUDED_FILTER );
                }
                else
                {
                    addLibraryJar( artifact.getFile().getAbsolutePath() );
                }
            }
            else
            {
                if ( isShiftedArtifact( artifact ) )
                {
                    // this is a blacklisted artifact that should be processed as a library instead
                    addLibraryJar( artifact.getFile().getAbsolutePath() );
                }
            }
        }
    }

    private boolean isMacOSXJDKbyApple( String javaHome )
    {
        return ( javaHome.startsWith( "/System/Library/Java" ) || javaHome.startsWith( "/Library/Java" ) )
                && new File( appleJDKLibsPath( javaHome ) ).exists();
    }

    private String appleJDKLibsPath( String javaHome )
    {
        return javaHome + "/../Classes";
    }

    /**
     * Get the path to the proguard jar.
     *
     * @return
     * @throws MojoExecutionException
     */
    private String getProguardJarPath() throws MojoExecutionException
    {
        String proguardJarPath = getProguardJarPathFromDependencies();
        if ( StringUtils.isEmpty( proguardJarPath ) )
        {
        	 File proguardJarPathFile = new File( getAndroidSdk().getToolsPath(), "proguard/lib/proguard.jar" );
        	 return proguardJarPathFile.getAbsolutePath();
        }
        return proguardJarPath;
    }

    private String getProguardJarPathFromDependencies() throws MojoExecutionException
    {
        Artifact proguardArtifact = null;
        int proguardArtifactDistance = - 1;
        for ( Artifact artifact : pluginDependencies )
        {
            getLog().debug( "pluginArtifact: " + artifact.getFile() );
            if ( ( "proguard".equals( artifact.getArtifactId() ) ) || ( "proguard-base"
                                                                                .equals( artifact.getArtifactId() ) ) )
            {
                int distance = artifact.getDependencyTrail().size();
                getLog().debug( "proguard DependencyTrail: " + distance );
                if ( proguardArtifactDistance == - 1 )
                {
                    proguardArtifact = artifact;
                    proguardArtifactDistance = distance;
                }
                else
                {
                    if ( distance < proguardArtifactDistance )
                    {
                        proguardArtifact = artifact;
                        proguardArtifactDistance = distance;
                    }
                }
            }
        }
        if ( proguardArtifact != null )
        {
            getLog().debug( "proguardArtifact: " + proguardArtifact.getFile() );
            return proguardArtifact.getFile().getAbsoluteFile().toString();
        }
        else
        {
            return null;
        }

    }

    /**
     * Get the default JVM arguments for the proguard invocation.
     *
     * @return
     * @see #parsedJvmArguments
     */
    private String[] getDefaultJvmArguments()
    {
        return new String[]{ "-Xmx512M" };
    }

    /**
     * Get the default ProGuard config files.
     *
     * @return
     * @see #parsedConfigs
     */
    private String[] getDefaultProguardConfigs()
    {
        return new String[0];
    }

	public static boolean isProGuardSkipProp() {
		return proGuardSkipProp;
	}

	public static void setProGuardSkipProp(boolean proGuardSkipProp) {
		ProguardMojo.proGuardSkipProp = proGuardSkipProp;
	}

}
