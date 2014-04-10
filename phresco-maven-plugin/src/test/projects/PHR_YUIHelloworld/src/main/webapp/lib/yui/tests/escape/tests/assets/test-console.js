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
YUI.add('test-console', function (Y) {

    new Y.Console({
        style: 'block',
        width: Y.UA.ie ? '100%' : 'inherit',

        entryTemplate:
            '<div class="{entry_class} {cat_class} {src_class}">' +
                '<pre class="{entry_content_class}">{message}</pre>' +
            '</div>',

        on: {
            entry: function (e) {
                var m = e.message,
                    node;

                if (m.category === 'info' &&
                    /\s(?:case|suite)\s|yuitests\d+|began/.test(m.message)) {
                        m.category = 'status';
                } else if (m.category === 'fail') {
                    this.printBuffer();
                    m.category = 'error';
                }
            }
        },

        after: {
            render: function () {
                this.get('contentBox').insertBefore(this._foot, this._body);
            }
        }
    }).plug(Y.Plugin.ConsoleFilters, {
        category: {
            pass: false,
            status: false
        }
    }).render('#log');
    
}, '@VERSION@', {requires: ['console-filters']});
