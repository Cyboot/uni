#include "main.h"

using namespace std;

float gaus(float x, float sigma) {
	float base = 1 / (sigma * sqrt(2 * M_PI));
	float exponent = -x * x / (2 * sigma * sigma);

	return base * exp(exponent);
}

CMatrix<float> computerFilterXGaus(CMatrix<float> img, int factor) {
	CMatrix<float> result = img;

	for (int y = 0; y < img.ySize(); ++y) {
		for (int x = 0; x < img.xSize(); ++x) {

			float weightAVG = 0;
			for (int dx = -factor*3; dx <= factor*3; dx++) {
				int index = x + dx;

				//Neumann Boundary cond --> mirror
				if (index < 0)
					index = -index;
				if (index > img.xSize())
					index = img.xSize() * 2 - index;

				weightAVG += (img(index, y) / 255.0) * gaus(dx, factor);
			}

			//set pixel to avg of surrounding pixel
			result(x, y) = weightAVG * 255;
		}
	}

	return result;
}

CMatrix<float> computerFilterYGaus(CMatrix<float> img, int factor) {
	CMatrix<float> result = img;

	for (int x = 0; x < img.xSize(); ++x) {
		for (int y = 0; y < img.ySize(); ++y) {

			float weightAVG = 0;
			for (int dy = -factor*3; dy <= factor*3; dy++) {
				int index = y + dy;

				//Neumann Boundary cond --> mirror
				if (index < 0)
					index = -index;
				if (index > img.ySize())
					index = img.ySize() * 2 - index;

				weightAVG += (img(x, index) / 255.0) * gaus(dy, factor);
			}

			//set pixel to avg of surrounding pixel
			result(x, y) = weightAVG * 255;
		}
	}

	return result;
}

int mainFilterGaus() {
	CMatrix<float> orignImage, filterImage;
	orignImage.readFromPGM("chinaToilet.pgm");

	filterImage = computerFilterXGaus(orignImage, 1);
	filterImage = computerFilterYGaus(filterImage, 1);

	filterImage.writeToPGM("chinaToilet-gausfilter.pgm");

	cout << "Gaus-Filter finish!" << endl;

	return 0;
}

