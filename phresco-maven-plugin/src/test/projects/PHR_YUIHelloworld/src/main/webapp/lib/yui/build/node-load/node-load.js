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
YUI.add('node-load', function(Y) {

/**
 * Extended Node interface with a basic IO API.
 * @module node
 * @submodule node-load
 */

/**
 * The default IO complete handler.
 * @method _ioComplete
 * @protected
 * @for Node
 * @param {String} code The response code.
 * @param {Object} response The response object.
 * @param {Array} args An array containing the callback and selector
 */

Y.Node.prototype._ioComplete = function(code, response, args) {
    var selector = args[0],
        callback = args[1],
        tmp,
        content;

    if (response && response.responseText) {
        content = response.responseText;
        if (selector) {
            tmp = Y.DOM.create(content);
            content = Y.Selector.query(selector, tmp);
        }
        this.setContent(content);
    }
    if (callback) {
        callback.call(this, code, response);
    }
};

/**
 * Loads content from the given url and replaces the Node's
 * existing content with the remote content.
 * @method load
 * @param {String} url The URL to load via XMLHttpRequest.
 * @param {String} selector An optional selector representing a subset of an HTML document to load.
 * @param {Function} callback An optional function to run after the content has been loaded.
 * @chainable
 */
Y.Node.prototype.load = function(url, selector, callback) {
    if (typeof selector == 'function') {
        callback = selector;
        selector = null;
    }
    var config = {
        context: this,
        on: {
            complete: this._ioComplete
        },
        arguments: [selector, callback]
    };

    Y.io(url, config);
    return this;
}


}, '3.4.1' ,{requires:['node-base', 'io-base']});
