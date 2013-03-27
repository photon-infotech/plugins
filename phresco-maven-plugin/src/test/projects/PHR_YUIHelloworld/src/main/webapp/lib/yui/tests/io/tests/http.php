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
	switch ($_GET['a']) {
		case '200':
			header('HTTP/1.1 200 OK');
			$state = 'success';
			break;
		case '204':
			header('HTTP/1.1 204 No Content');
			break;
		case '304':
			header('HTTP/1.1 304 Not Modified');
			break;
		case '404':
			header('HTTP/1.1 404 Not Found');
			$state = 'failure';
			break;
		case '500':
			header('HTTP/1.1 500 Server Error');
			$state = 'failure';
			break;
		case '999':
			header('HTTP/1.1 999 Unknown');
			$state = 'exception';
			break;
	}

echo $state;
?>