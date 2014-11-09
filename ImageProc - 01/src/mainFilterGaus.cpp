#include "main.h"

using namespace std;

float gaus(float x, float sigma) {
	float base = 1 / (sigma * sqrt(2 * M_PI));
	float exponent = -x * x / (2 * sigma * sigma);

	return base * exp(exponent);
}

CMatrix<float> computerFilterXGaus(CMatrix<float> img, int factor) {
	const int FACTOR_DELTA = 5;

	CMatrix<float> result = img;

	for (int y = 0; y < img.ySize(); ++y) {
		for (int x = 0; x < img.xSize(); ++x) {

			float weightAVG = 0;
			float sumAVG = 0;
			for (int dx = -factor * FACTOR_DELTA; dx <= factor * FACTOR_DELTA;
					dx++) {
				int indexX = x + dx;

				//Neumann Boundary cond --> mirror
				if (indexX < 0)
					indexX = -indexX - 1;
				if (indexX >= img.xSize())
					indexX = img.xSize() * 2 - indexX - 1;

				for (int dy = -factor * FACTOR_DELTA;
						dy <= factor * FACTOR_DELTA; dy++) {
					int indexY = y + dy;

					//Neumann Boundary cond --> mirror
					if (indexY < 0)
						indexY = -indexY - 1;
					if (indexY >= img.ySize())
						indexY = img.ySize() * 2 - indexY - 1;

					float color = img(indexX, indexY);
					float gausWeight = gaus(sqrt(dx * dx + dy * dy), factor);
					sumAVG += gausWeight;

					weightAVG += (color / 255.0) * gausWeight;
				}
			}

			//set pixel to avg of surrounding pixel
			result(x, y) = weightAVG / sumAVG * 255;
			if (result(x, y) < 0)
				result(x, y) = 0;
			if (result(x, y) > 255)
				result(x, y) = 255;

//			cout << x << ":" << y << " = " << img(x, y) << " >> "
//					<< result(x, y) << endl;
		}
	}

	return result;
}

CMatrix<float> computerFilterYGaus(CMatrix<float> img, int factor) {
	CMatrix<float> result = img;

	for (int x = 0; x < img.xSize(); ++x) {
		for (int y = 0; y < img.ySize(); ++y) {

			float weightAVG = 0;
			for (int dy = -factor * 3; dy <= factor * 3; dy++) {
				int indexY = y + dy;

				//Neumann Boundary cond --> mirror
				if (indexY < 0)
					indexY = -indexY;
				if (indexY > img.ySize())
					indexY = img.ySize() * 2 - indexY;

				weightAVG += (img(x, indexY) / 255.0) * gaus(dy, factor);
			}

			//set pixel to avg of surrounding pixel
			result(x, y) = weightAVG * 255;
		}
	}

	return result;
}

int mainFilterGaus() {
	CMatrix<float> orignImage, filterImage;
	orignImage.readFromPGM("small.pgm");

	filterImage = computerFilterXGaus(orignImage, 3);
//	filterImage = computerFilterYGaus(filterImage, 1);

	filterImage.writeToPGM("gaus-result.pgm");

	cout << "Gaus-Filter finish!" << endl;

	return 0;
}

