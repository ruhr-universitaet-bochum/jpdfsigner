/*
 * JPDFSigner - Sign PDFs online using smartcards (TextStroke.java)
 * Copyright (C) 2013  Ruhr-Universitaet Bochum - Daniel Moczarski, Haiko te Neues
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl.txt>.
 *
 */

/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package de.rub.dez6a3.jpdfsigner.control;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;

public class TextStroke implements Stroke {
	private String text;
	private Font font;
	private boolean stretchToFit = false;
	private boolean repeat = false;
	private AffineTransform t = new AffineTransform();

	private static final float FLATNESS = 1;

	public TextStroke( String text, Font font ) {
		this( text, font, true, false );
	}

	public TextStroke( String text, Font font, boolean stretchToFit, boolean repeat ) {
		this.text = text;
		this.font = font;
		this.stretchToFit = stretchToFit;
		this.repeat = repeat;
	}

	public Shape createStrokedShape( Shape shape ) {
		FontRenderContext frc = new FontRenderContext(null, true, true);
		GlyphVector glyphVector = font.createGlyphVector(frc, text);

		GeneralPath result = new GeneralPath();
		PathIterator it = new FlatteningPathIterator( shape.getPathIterator( null ), FLATNESS );
		float points[] = new float[6];
		float moveX = 0, moveY = 0;
		float lastX = 0, lastY = 0;
		float thisX = 0, thisY = 0;
		int type = 0;
		boolean first = false;
		float next = 0;
		int currentChar = 0;
		int length = glyphVector.getNumGlyphs();

		if ( length == 0 )
            return result;

        float factor = stretchToFit ? measurePathLength( shape )/(float)glyphVector.getLogicalBounds().getWidth() : 1.0f;
        float nextAdvance = 0;

		while ( currentChar < length && !it.isDone() ) {
			type = it.currentSegment( points );
			switch( type ){
			case PathIterator.SEG_MOVETO:
				moveX = lastX = points[0];
				moveY = lastY = points[1];
				result.moveTo( moveX, moveY );
				first = true;
                nextAdvance = glyphVector.getGlyphMetrics( currentChar ).getAdvance() * 0.5f;
                next = nextAdvance;
				break;

			case PathIterator.SEG_CLOSE:
				points[0] = moveX;
				points[1] = moveY;
				// Fall into....

			case PathIterator.SEG_LINETO:
				thisX = points[0];
				thisY = points[1];
				float dx = thisX-lastX;
				float dy = thisY-lastY;
				float distance = (float)Math.sqrt( dx*dx + dy*dy );
				if ( distance >= next ) {
					float r = 1.0f/distance;
					float angle = (float)Math.atan2( dy, dx );
					while ( currentChar < length && distance >= next ) {
						Shape glyph = glyphVector.getGlyphOutline( currentChar );
						Point2D p = glyphVector.getGlyphPosition(currentChar);
						float px = (float)p.getX();
						float py = (float)p.getY();
						float x = lastX + next*dx*r;
						float y = lastY + next*dy*r;
                        float advance = nextAdvance;
                        nextAdvance = currentChar < length-1 ? glyphVector.getGlyphMetrics(currentChar+1).getAdvance() * 0.5f : 0;
						t.setToTranslation( x, y );
						t.rotate( angle );
						t.translate( -px-advance, -py );
						result.append( t.createTransformedShape( glyph ), false );
						next += (advance+nextAdvance) * factor;
						currentChar++;
						if ( repeat )
							currentChar %= length;
					}
				}
                next -= distance;
				first = false;
				lastX = thisX;
				lastY = thisY;
				break;
			}
			it.next();
		}

		return result;
	}

	public float measurePathLength( Shape shape ) {
		PathIterator it = new FlatteningPathIterator( shape.getPathIterator( null ), FLATNESS );
		float points[] = new float[6];
		float moveX = 0, moveY = 0;
		float lastX = 0, lastY = 0;
		float thisX = 0, thisY = 0;
		int type = 0;
        float total = 0;

		while ( !it.isDone() ) {
			type = it.currentSegment( points );
			switch( type ){
			case PathIterator.SEG_MOVETO:
				moveX = lastX = points[0];
				moveY = lastY = points[1];
				break;

			case PathIterator.SEG_CLOSE:
				points[0] = moveX;
				points[1] = moveY;
				// Fall into....

			case PathIterator.SEG_LINETO:
				thisX = points[0];
				thisY = points[1];
				float dx = thisX-lastX;
				float dy = thisY-lastY;
				total += (float)Math.sqrt( dx*dx + dy*dy );
				lastX = thisX;
				lastY = thisY;
				break;
			}
			it.next();
		}

		return total;
	}

}