#include "main.h"

using namespace std;

CMatrix<float> computerFilterX(CMatrix<float> img, int factor) {
	CMatrix<float> result = img;

	for (int y = 0; y < img.ySize(); ++y) {
		for (int x = 0; x < img.xSize(); ++x) {

			float sum = 0;
			for (int dx = -factor; dx <= factor; dx++) {
				int index = x + dx;

				//Neumann Boundary cond --> mirror
				if (index < 0)
					index = -index;
				if (index > img.xSize())
					index = img.xSize() * 2 - index;

				sum += img(index, y);
			}

			//set pixel to avg of surrounding pixel
			result(x, y) = sum / (2 * factor + 1);
		}
	}

	return result;
}
CMatrix<float> computerFilterY(CMatrix<float> img, int factor) {
	CMatrix<float> result = img;

	for (int x = 0; x < img.xSize(); ++x) {
		for (int y = 0; y < img.ySize(); ++y) {

			float sum = 0;
			for (int dy = -factor; dy <= factor; dy++) {
				int index = y + dy;

				//Neumann Boundary cond --> mirror
				if (index < 0)
					index = -index;
				if (index > img.ySize())
					index = img.ySize() * 2 - index;

				sum += img(x, index);
			}

			//set pixel to avg of surrounding pixel
			result(x, y) = sum / (2 * factor + 1);
		}
	}

	return result;
}

int mainFilterBox() {
	CMatrix<float> orignImage, filterImage;
	orignImage.readFromPGM("chinaToilet.pgm");


	filterImage = computerFilterX(orignImage,10);
	filterImage = computerFilterY(filterImage,10);

	filterImage.writeToPGM("chinaToilet-boxfilter.pgm");

	cout << "Box-Filter finish!" << endl;

	return 0;
}
