<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>JsTest Runner</title>
    <script type="text/javascript">
            exports = window;
    </script>   
    
    <script type="text/javascript" src="/runnerResource/jstest-common.js"></script>
    
    <!-- test Resources ...... -->
    $testResources$

    <!-- sources...... -->
	 $sources$

</head>
<body class="yui3-skin-sam">

<div id="log"></div>

    <script type="text/javascript">
		YUI({
		    coverage: ['test'],
		    filter: 'raw',
		    modules: {
		        $modules$
		    }
		}).use($srcNames$, $testNames$, 'test-console', 'test', function(Y) {

		
		    new Y.Test.Console().render('#log');
		    var TestRunner = Y.Test.Runner;
		    var YTest = Y.Test;
		    
	        var testManager = new TestManager($debug$, $serverMode$, $browserId$, "$runId$", $emulator$);
	        testManager.run(TestRunner, YTest);
		});
		
    </script>
</body>
</html>

