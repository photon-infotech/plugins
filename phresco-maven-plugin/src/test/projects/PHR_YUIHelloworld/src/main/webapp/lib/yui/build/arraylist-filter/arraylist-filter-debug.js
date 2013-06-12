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
YUI.add('arraylist-filter', function(Y) {

/**
 * Collection utilities beyond what is provided in the YUI core
 * @module collection
 * @submodule arraylist-filter
 */

/*
 * Adds filter method to ArrayList prototype
 */
Y.mix(Y.ArrayList.prototype, {

    /**
     * <p>Create a new ArrayList (or augmenting class instance) from a subset
     * of items as determined by the boolean function passed as the
     * argument.  The original ArrayList is unchanged.</p>
     *
     * <p>The validator signature is <code>validator( item )</code>.</p>
     *
     * @method filter
     * @param { Function } validator Boolean function to determine in or out.
     * @return { ArrayList } New instance based on who passed the validator.
     * @for ArrayList
     */
    filter: function(validator) {
        var items = [];

        Y.Array.each(this._items, function(item, i) {
            item = this.item(i);

            if (validator(item)) {
                items.push(item);
            }
        }, this);

        return new this.constructor(items);
    }

});


}, '3.4.1' ,{requires:['arraylist']});
