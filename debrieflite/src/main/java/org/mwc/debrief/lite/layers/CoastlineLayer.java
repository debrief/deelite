/*******************************************************************************
 * Copyright (c) 2014, PlanetMayo Ltd. All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package org.mwc.debrief.lite.layers;

import com.bbn.openmap.layer.shape.ShapeLayer;

/**
 * @author snpe
 *
 */
public class CoastlineLayer extends ShapeLayer {
	
	private static final long serialVersionUID = 1L;

	public CoastlineLayer(String fileName) {
		super(fileName);
		setName("Coastline");
//		setProjectionChangePolicy(new StandardPCPolicy(
//				this, true));
//		BufferedImageRenderPolicy policy = new BufferedImageRenderPolicy();
//		Map<Key, Object> map = new HashMap<RenderingHints.Key, Object>();
//		map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
//		RenderingHints rh = new RenderingHints(map);
//		policy.setRenderingHints(rh);
//		setRenderPolicy(policy);
	}
}
