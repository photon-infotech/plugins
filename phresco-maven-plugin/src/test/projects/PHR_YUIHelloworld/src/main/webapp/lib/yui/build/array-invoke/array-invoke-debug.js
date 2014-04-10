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
YUI.add('array-invoke', function(Y) {

/**
@module collection
@submodule array-invoke
*/

/**
Executes a named method on each item in an array of objects. Items in the array
that do not have a function by that name will be skipped.

@example

    Y.Array.invoke(arrayOfDrags, 'plug', Y.Plugin.DDProxy);

@method invoke
@param {Array} items Array of objects supporting the named method.
@param {String} name the name of the method to execute on each item.
@param {Any} [args*] Any number of additional args are passed as parameters to
  the execution of the named method.
@return {Array} All return values, indexed according to the item index.
@static
@for Array
**/
Y.Array.invoke = function(items, name) {
    var args = Y.Array(arguments, 2, true),
        isFunction = Y.Lang.isFunction,
        ret = [];

    Y.Array.each(Y.Array(items), function(item, i) {
        if (isFunction(item[name])) {
            ret[i] = item[name].apply(item, args);
        }
    });

    return ret;
};


}, '3.4.1' ,{requires:['yui-base']});
