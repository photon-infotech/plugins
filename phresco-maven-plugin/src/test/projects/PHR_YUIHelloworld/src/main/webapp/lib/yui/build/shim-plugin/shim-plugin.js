/*
 * Phresco Maven Plugin
 *
 * Copyright (C) 1999-2013 Photon Infotech Inc.
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
YUI.add('shim-plugin', function(Y) {

    /**
     * Provides shimming support for Node via a Plugin.
     * This fixes SELECT bleedthrough for IE6 & Mac scrollbars
     * @module shim-plugin
     */

    /**
     * Node plugin which can be used to add shim support.
     *
     * @class Plugin.Shim
     * @param {Object} User configuration object
     */
    function Shim(config) {
        this.init(config);
    }

    /**
     * Default class used to mark the shim element
     *
     * @property CLASS_NAME
     * @type String
     * @static
     * @default "yui-node-shim"
     */
    // TODO: use ClassNameManager
    Shim.CLASS_NAME = 'yui-node-shim';

    /**
     * Default markup template used to generate the shim element.
     * 
     * @property TEMPLATE
     * @type String
     * @static
     */
    Shim.TEMPLATE = '<iframe class="' + Shim.CLASS_NAME +
            '" frameborder="0" title="Node Stacking Shim"' +
            'src="javascript:false" tabindex="-1" role="presentation"' +
            'style="position:absolute; z-index:-1;"></iframe>';

    Shim.prototype = {
        init: function(config) {
            this._host = config.host;
            this.initEvents();
            this.insert();
            this.sync();
        },

        initEvents: function() {
            this._resizeHandle = this._host.on('resize', this.sync, this);
        },
        
        getShim: function() {
            return this._shim || (
                this._shim = Y.Node.create(
                    Shim.TEMPLATE,
                    this._host.get('ownerDocument')
                )
            );
        },

        insert: function() {
            var node = this._host;
            this._shim = node.insertBefore( this.getShim(),
                    node.get('firstChild'));
        },

        /**
         * Updates the size of the shim to fill its container
         * @method sync
         */
        sync: function() {
            var shim = this._shim,
                node = this._host;

            if (shim) {
                shim.setAttrs({
                    width: node.getStyle('width'),
                    height: node.getStyle('height')
                });
            }
        },

        /**
         * Removes the shim and destroys the plugin
         * @method destroy
         */
        destroy: function() {
            var shim = this._shim;
            if (shim) {
                shim.remove(true);
            }

            this._resizeHandle.detach();
        }
    };

    Shim.NAME = 'Shim';
    Shim.NS = 'shim';

    Y.namespace('Plugin');
    Y.Plugin.Shim = Shim;


}, '3.4.1' ,{requires:['node-style', 'node-pluginhost']});
