<?php
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

$callback = $_GET['callback'];

if (isset($_GET['wait'])) {
    sleep($_GET['wait']); // in seconds
}

header('content-type: text/javascript');
header('Cache-Control: no-store;max-age:0');
header('Expires: Tue 10 Mar 1989 03:10:00 GMT');

echo $callback . '({"data":"here","callback":"'.$callback.'"})';
?>
