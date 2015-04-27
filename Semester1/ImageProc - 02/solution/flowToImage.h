/**
 * Nikolaus Mayer, 2014 (mayern@cs.uni-freiburg.de)
 *
 * Image Processing and Computer Graphics
 * Winter Term 2014/2015
 * Exercise Sheet 2 (Image Processing part)
 */

#ifndef FLOWTOIMAGE_H__
#define FLOWTOIMAGE_H__

#include "../lib/CMatrix.h"
#include "../lib/CTensor.h"

void cartesianToRGB (float x, float y, float& R, float& G, float& B);

void flowToImage(CTensor<float>& aFlow, CTensor<float>& aImage);


#endif  // FLOWTOIMAGE_H__

