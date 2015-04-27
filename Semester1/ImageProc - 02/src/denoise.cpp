#include "main.h"

int getIndex(CMatrix<float> img, int x, int y) {
	int maxX = img.xSize();
	int maxY = img.ySize();

	int index = maxX * y + x;

	if (index < 0)
		return -1;
	if (index >= maxX * maxY)
		return -1;

	return index;
}

void mainDenoise() {
	CMatrix<float> img;
	img.readFromPGM("img/small.pgm");

	float alpha = 5;
	float omega = 1.8;

	float u[img.xSize() * img.ySize()];
	for (int i = 0; i < img.xSize() * img.ySize(); ++i) {
		u[i] = 0;
	}

	for (int iteration = 0; iteration < 10; ++iteration) {
		for (int y = 0; y < img.ySize(); ++y) {
			for (int x = 0; x < img.xSize(); ++x) {
				float left = 0, right = 0, up = 0, down = 0;

				int alphaFactor = 0;

				int index;
				index = getIndex(img, x - 1, y);
				if (index != -1) {
					left = u[index];
					alphaFactor++;
				}

				index = getIndex(img, x + 1, y);
				if (index != -1) {
					right = u[index];
					alphaFactor++;
				}

				index = getIndex(img, x, y - 1);
				if (index != -1) {
					up = u[index];
					alphaFactor++;
				}

				index = getIndex(img, x, y + 1);
				if (index != -1) {
					down = u[index];
					alphaFactor++;
				}

				float pixel = img(x, y);

				float valueBefore = u[getIndex(img, x, y)];
				float valueNow = (alpha * left + alpha * right + alpha * up + alpha * down + pixel)
						/ (1 + alphaFactor * alpha);

				// SOR step
				u[getIndex(img, x, y)] = (1 - omega) * valueBefore + omega * valueNow;
			}
		}
		//For reference: "correct value" = 63.6741
		cout << "0:0  -->  " << u[0] << endl;
		cout << "Iteration: " << iteration << endl;
	}

	CMatrix<float> smoothImg(img.xSize(), img.ySize());

	for (int i = 0; i < img.xSize() * img.ySize(); ++i) {
		smoothImg.data()[i] = u[i];
	}

	smoothImg.writeToPGM("img/smooth.pgm");

	cout << "Denoise finished!" << endl;
}

