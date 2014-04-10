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
var http = require('http');
var url = require('url');
var fs = require('fs');
var path = require('path');

var EXT_TO_TYPE = {
    "js" : "text/javascript",
    "css" : "text/css"
}

function rand(min, max) {
   return Math.floor(Math.random() * (max - min) + min);
}

function send(res, code, content, type) {
    res.writeHead(code, { 'Content-Type': type });
    res.end(content);
}

function sendResponse(res, filepath, delay) {

    var DEFAULT_JS_CONTENT = "console.log('Server Delay:" + delay + "');";

    if (filepath === '/') {
        send(res, 200, DEFAULT_JS_CONTENT, EXT_TO_TYPE["js"]);
    } else {
        var extension = path.extname(filepath).substring(1);
        var contentType = EXT_TO_TYPE[extension] || "text/html";

        // Convert to "server" root
        var filepath = __dirname + filepath;

        path.exists(filepath, function(exists) {
            if (exists) {
                fs.readFile(filepath, function(e, content) {
                    if (!e) {
                        content = "/* Delayed: " + delay + "*/\n" + content;
                        send(res, 200, content, contentType);
                    } else {
                        send(res, 500, "Error", "text/html");
                    }
                });
            } else {
                send(res, 404, "File Not Found", "text/html");
            }
        });
    }
}

http.createServer(function (req, res) {

    var parts = url.parse(req.url, true);

    console.log("Request:" + parts.pathname);

    if (parts.pathname.indexOf("favicon.ico") !== -1) {
        res.writeHead(404);
        res.end("");
    } else {
        var delay = parts.query.delay || rand(100, 2000);

        console.log("delay:" + delay);

        setTimeout(function() {
            sendResponse(res, parts.pathname, delay);
        }, delay);
    }

}).listen(8014);

console.log('Server running on port 8014');

