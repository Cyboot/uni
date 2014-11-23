#include "main.h"

float omega = 1.95;
float alpha = 5000;

float calcU(CMatrix<float> ix, CMatrix<float> iy, CMatrix<float> it, float* u, float* v, int x,
		int y) {
	float left = 0, right = 0, up = 0, down = 0;

	int alphaFactor = 0;

	int index;
	index = getIndex(ix, x - 1, y);
	if (index != -1) {
		left = u[index];
		alphaFactor++;
	}

	index = getIndex(ix, x + 1, y);
	if (index != -1) {
		right = u[index];
		alphaFactor++;
	}

	index = getIndex(ix, x, y - 1);
	if (index != -1) {
		up = u[index];
		alphaFactor++;
	}

	index = getIndex(ix, x, y + 1);
	if (index != -1) {
		down = u[index];
		alphaFactor++;
	}

	//b = right side
	float b = -1 / alpha * ix(x, y) * it(x, y);

	// off diagonal part
	float vPart = (1 / alpha) * ix(x, y) * iy(x, y) * v[getIndex(ix, x, y)];
	float M = left + right + up + down - vPart;

	float valueBefore = u[getIndex(ix, x, y)];
	float valueNow = (b + M) / (alphaFactor + 1 / alpha * ix(x, y) * ix(x, y));

	return (1 - omega) * valueBefore + omega * valueNow;
}

float calcV(CMatrix<float> ix, CMatrix<float> iy, CMatrix<float> it, float* u, float* v, int x,
		int y) {
	float left = 0, right = 0, up = 0, down = 0;

	int alphaFactor = 0;

	int index;
	index = getIndex(ix, x - 1, y);
	if (index != -1) {
		left = v[index];
		alphaFactor++;
	}

	index = getIndex(ix, x + 1, y);
	if (index != -1) {
		right = v[index];
		alphaFactor++;
	}

	index = getIndex(ix, x, y - 1);
	if (index != -1) {
		up = v[index];
		alphaFactor++;
	}

	index = getIndex(ix, x, y + 1);
	if (index != -1) {
		down = v[index];
		alphaFactor++;
	}

	//b = right side
	float b = -1 / alpha * iy(x, y) * it(x, y);

	// off diagonal part
	float uPart = 1 / alpha * ix(x, y) * iy(x, y) * u[getIndex(ix, x, y)];
	float M = left + right + up + down - uPart;

	float valueBefore = v[getIndex(ix, x, y)];
	float valueNow = (b + M) / (alphaFactor + 1 / alpha * iy(x, y) * iy(x, y));

	return (1 - omega) * valueBefore + omega * valueNow;
}

void mainHornSchunk() {
	CMatrix<float> img0, img1;
//	img0.readFromPGM("img/big0.pgm");
//	img1.readFromPGM("img/big1.pgm");
//	img0.readFromPGM("img/car0.pgm");
//	img1.readFromPGM("img/car1.pgm");
	img0.readFromPGM("img/cropped-street_000009.pgm");
	img1.readFromPGM("img/cropped-street_000010.pgm");
	img0.readFromPGM("img/yos008.pgm");
	img1.readFromPGM("img/yos009.pgm");

	CMatrix<float> ix, iy, it;
	ix = deriveX(img0);
	iy = deriveY(img0);
	it = deriveT(img0, img1);

	float u[img0.xSize() * img0.ySize()];
	float v[img0.xSize() * img0.ySize()];
	for (int i = 0; i < img0.xSize() * img0.ySize(); ++i) {
		u[i] = 0;
		v[i] = 0;
	}

	for (int iteration = 0; iteration < 5; ++iteration) {

		for (int y = 0; y < img0.ySize(); ++y) {
			for (int x = 0; x < img0.xSize(); ++x) {
				float valueU = calcU(ix, iy, it, u, v, x, y);
				u[getIndex(img0, x, y)] = valueU;

				float valueV = calcV(ix, iy, it, u, v, x, y);
				v[getIndex(img0, x, y)] = valueV;

//				cout << u[getIndex(img0, x, y)] << endl;
			}
		}

		cout << "Iteration: " << iteration << endl;
	}

	CMatrix<float> moveImg(img0.xSize(), img0.ySize());

	for (int i = 0; i < img0.xSize() * img0.ySize(); ++i) {
		moveImg.data()[i] = u[i];
	}
	cout << "Min: " << moveImg.min() << "   Max: " << moveImg.max() << endl;

	moveImg = normalize(moveImg);
	moveImg.writeToPGM("img/moveHS.pgm");

	cout << "Horn-Schunk finished" << endl;
}
