/*
 * Phresco Maven Plugin
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
/*
YUI 3.4.1 (build 4118)
Copyright 2011 Yahoo! Inc. All rights reserved.
Licensed under the BSD License.
http://yuilibrary.com/license/
*/
YUI.add('base-pluginhost', function(Y) {

    /**
     * The base-pluginhost submodule adds Plugin support to Base, by augmenting Base with 
     * Plugin.Host and setting up static (class level) Base.plug and Base.unplug methods.
     *
     * @module base
     * @submodule base-pluginhost
     * @for Base
     */

    var Base = Y.Base,
        PluginHost = Y.Plugin.Host;

    Y.mix(Base, PluginHost, false, null, 1);

    /**
     * Alias for <a href="Plugin.Host.html#method_Plugin.Host.plug">Plugin.Host.plug</a>. See aliased 
     * method for argument and return value details.
     *
     * @method plug
     * @static
     */
    Base.plug = PluginHost.plug;

    /**
     * Alias for <a href="Plugin.Host.html#method_Plugin.Host.unplug">Plugin.Host.unplug</a>. See the 
     * aliased method for argument and return value details.
     *
     * @method unplug
     * @static
     */
    Base.unplug = PluginHost.unplug;


}, '3.4.1' ,{requires:['base-base', 'pluginhost']});
