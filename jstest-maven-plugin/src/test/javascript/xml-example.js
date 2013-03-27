/*
 * JsTest Maven Plugin
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
xdescribe('Slice-o-matic',function(){
	
	it('occupies the SliceOMatic namespace',function(){});
	
	describe('#slice',function() {
		
		it('slices',function() {});
		
		it('does not dice',function(){});
		
		xit('prevents cuts to your thumb',function(){});
	});
	
	describe('#dice',function() {
		
		describe('when the onion is peeled',function() {
			it('dices evenly-sized cubes',function() {});			
		});
		
		describe('when the knob is turned to "Fine"',function() {
			it('dices quite finely',function() {});
			it("does not cut off fingers", function() {});
		});
		
		describe('when the knob is turned to "Coarse"',function() {
			it('dices rather roughly',function() {});
		});
		
		describe("when a hand is inserted into the Slice-o-matic", function() {
		  it("is a fantastic idea", function() {
					expect("Are you kidding? That's a terrible idea!").toContain('Great idea');
		  });
		});
	});
});
