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
YUI.add('createlink-base', function(Y) {


    /**
     * Adds prompt style link creation. Adds an override for the <a href="Plugin.ExecCommand.html#method_COMMANDS.createlink">createlink execCommand</a>.
     * @class Plugin.CreateLinkBase
     * @static
     * @submodule createlink-base
     * @module editor
     */
    
    var CreateLinkBase = {};
    /**
    * Strings used by the plugin
    * @property STRINGS
    * @static
    */
    CreateLinkBase.STRINGS = {
            /**
            * String used for the Prompt
            * @property PROMPT
            * @static
            */
            PROMPT: 'Please enter the URL for the link to point to:',
            /**
            * String used as the default value of the Prompt
            * @property DEFAULT
            * @static
            */
            DEFAULT: 'http://'
    };

    Y.namespace('Plugin');
    Y.Plugin.CreateLinkBase = CreateLinkBase;

    Y.mix(Y.Plugin.ExecCommand.COMMANDS, {
        /**
        * Override for the createlink method from the <a href="Plugin.CreateLinkBase.html">CreateLinkBase</a> plugin.
        * @for ExecCommand
        * @method COMMANDS.createlink
        * @static
        * @param {String} cmd The command executed: createlink
        * @return {Node} Node instance of the item touched by this command.
        */
        createlink: function(cmd) {
            var inst = this.get('host').getInstance(), out, a, sel, holder,
                url = prompt(CreateLinkBase.STRINGS.PROMPT, CreateLinkBase.STRINGS.DEFAULT);

            if (url) {
                holder = inst.config.doc.createElement('div');
                url = url.replace(/"/g, '').replace(/'/g, ''); //Remove single & double quotes
                url = inst.config.doc.createTextNode(url);
                holder.appendChild(url);
                url = holder.innerHTML;


                this.get('host')._execCommand(cmd, url);
                sel = new inst.Selection();
                out = sel.getSelected();
                if (!sel.isCollapsed && out.size()) {
                    //We have a selection
                    a = out.item(0).one('a');
                    if (a) {
                        out.item(0).replace(a);
                    }
                    if (Y.UA.gecko) {
                        if (a.get('parentNode').test('span')) {
                            if (a.get('parentNode').one('br.yui-cursor')) {
                                a.get('parentNode').insert(a, 'before');
                            }
                        }
                    }
                } else {
                    //No selection, insert a new node..
                    this.get('host').execCommand('inserthtml', '<a href="' + url + '">' + url + '</a>');
                }
            }
            return a;
        }
    });



}, '3.4.1' ,{skinnable:false, requires:['editor-base']});
