#include "main.h"

int main(int argc, char** args) {
	CMatrix<float> img;
	img.readFromPGM("img/2x2.pgm");

	// Using NFilter & CDerivative to obtain the first derivative in X and Y
	CMatrix<float> ix(img.xSize(), img.ySize());
	CMatrix<float> iy(img.xSize(), img.ySize());
	// One parameter is a dummy, i.e. the one with value 1
	NFilter::filter<float>(img, ix, CDerivative<float>(5), 1);
	NFilter::filter<float>(img, iy, 1, CDerivative<float>(3));

	float min = ix.min();
	float max = ix.max();
	float varianz = max - min;


	for (int x = 0; x < ix.xSize(); ++x) {
		for (int y = 0; y < ix.ySize(); ++y) {
			float pix = ix(x,y) - min;
			pix = pix / varianz * 255;
			ix(x,y) = pix;
		}
	}

	ix.writeToPGM("img/ix.pgm");
}
