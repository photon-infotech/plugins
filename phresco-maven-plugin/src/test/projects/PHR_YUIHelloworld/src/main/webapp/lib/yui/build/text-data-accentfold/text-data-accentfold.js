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
YUI.add('text-data-accentfold', function(Y) {

// The following tool was very helpful in creating these mappings:
// http://unicode.org/cldr/utility/list-unicodeset.jsp?a=[:toNFKD%3D/^a/:]&abb=on

Y.namespace('Text.Data').AccentFold = {
    0: /[�?�₀⓪�?]/gi,
    1: /[¹�?①１]/gi,
    2: /[²₂②２]/gi,
    3: /[³₃③３]/gi,
    4: /[�?�₄④４]/gi,
    5: /[�?�₅⑤５]/gi,
    6: /[�?�₆⑥６]/gi,
    7: /[�?�₇⑦７]/gi,
    8: /[�?�₈⑧８]/gi,
    9: /[�?�₉⑨９]/gi,
    a: /[ªà-å�?ăąǎǟǡǻ�?ȃȧᵃ�?ẚạảấầẩẫậắằẳẵặ�?�?]/gi,
    b: /[ᵇḃḅḇⓑｂ]/gi,
    c: /[çćĉċ�?ᶜḉⓒｃ]/gi,
    d: /[�?ᵈḋ�?�?ḑḓⅾⓓｄ]/gi,
    e: /[è-ëēĕėęěȅȇȩᵉḕḗḙḛ�?ẹẻẽế�?ểễệₑℯⓔｅ]/gi,
    f: /[ᶠḟⓕｆ]/gi,
    g: /[�?ğġģǧǵ�?ḡℊⓖｇ]/gi,
    h: /[ĥȟʰḣḥḧḩḫẖℎⓗｈ]/gi,
    i: /[ì-ïĩīĭįĳ�?ȉȋᵢḭḯỉị�?�ℹⅰⓘｉ]/gi,
    j: /[ĵǰʲⓙⱼｊ]/gi,
    k: /[ķǩ�?ḱḳḵⓚｋ]/gi,
    l: /[ĺļľŀǉˡḷḹḻḽℓⅼⓛｌ]/gi,
    m: /[�?ḿ�?ṃⅿⓜ�?]/gi,
    n: /[ñńņňǹṅṇṉṋ�?��?ｎ]/gi,
    o: /[ºò-ö�?�?őơǒǫǭ�?�?ȫȭȯȱᵒ�?�?ṑṓ�?�?ốồổỗộớ�?ởỡợₒℴⓞ�?]/gi,
    p: /[ᵖṕṗⓟ�?]/gi,
    q: /[ʠⓠｑ]/gi,
    r: /[ŕŗřȑȓʳᵣṙṛ�?ṟⓡｒ]/gi,
    s: /[ś�?şšſșˢṡṣṥṧṩẛⓢｓ]/gi,
    t: /[ţťțᵗṫṭṯṱẗⓣｔ]/gi,
    u: /[ù-üũūŭůűųưǔǖǘǚǜȕȗᵘᵤṳṵṷṹṻụủứừửữựⓤｕ]/gi,
    v: /[ᵛᵥṽṿⅴⓥｖ]/gi,
    w: /[ŵʷ�?ẃẅẇẉẘⓦｗ]/gi,
    x: /[ˣẋ�?ₓⅹⓧｘ]/gi,
    y: /[ýÿŷȳʸ�?ẙỳỵỷỹⓨｙ]/gi,
    z: /[źżžᶻẑẓẕⓩｚ]/gi
};


}, '3.4.1' ,{requires:['yui-base']});
