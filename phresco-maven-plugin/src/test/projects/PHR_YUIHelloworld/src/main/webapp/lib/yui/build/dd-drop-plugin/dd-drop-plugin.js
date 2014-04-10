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
YUI.add('dd-drop-plugin', function(Y) {


       /**
        * Simple Drop plugin that can be attached to a Node via the plug method.
        * @module dd
        * @submodule dd-drop-plugin
        */
       /**
        * Simple Drop plugin that can be attached to a Node via the plug method.
        * @class Drop
        * @extends DD.Drop
        * @constructor
        * @namespace Plugin
        */


        var Drop = function(config) {
            config.node = config.host;
            Drop.superclass.constructor.apply(this, arguments);
        };
        
        /**
        * @property NAME
        * @description dd-drop-plugin
        * @type {String}
        */
        Drop.NAME = "dd-drop-plugin";
        /**
        * @property NS
        * @description The Drop instance will be placed on the Node instance under the drop namespace. It can be accessed via Node.drop;
        * @type {String}
        */
        Drop.NS = "drop";


        Y.extend(Drop, Y.DD.Drop);
        Y.namespace('Plugin');
        Y.Plugin.Drop = Drop;




}, '3.4.1' ,{skinnable:false, requires:['dd-drop']});
