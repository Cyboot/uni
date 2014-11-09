#include "main.h"

using namespace std;

float alpha = .25;
static float e_Alpha = exp(-alpha);
static float e_2Alpha = exp(-alpha * 2);

static float prefactor = pow((1 - e_Alpha), 2) / (1 + 2 * alpha * e_Alpha - e_2Alpha);

float f(float pix0, float pix1, float f1, float f2) {
	float part1 = pix0 + e_Alpha * (alpha - 1) * pix1;
	float part2 = (2 * e_Alpha * f1) - (e_2Alpha * f2);

	return prefactor * part1 + part2;
}

float g(float pix1, float pix2, float g1, float g2) {
	float part1 = (e_Alpha * (alpha + 1) * pix1) - (e_2Alpha * pix2);
	float part2 = 2 * e_Alpha * g1 - e_2Alpha * g2;

	return prefactor * part1 + part2;
}

CMatrix<float> filterRecursive(CMatrix<float> img, float alpha) {
	CMatrix<float> img_F = img, img_G = img, img_result = img;

	for (int y = 0; y < img.ySize(); ++y) {
		// F --> from left to right
		float f0 = 0;
		float f1 = 0;
		float f2 = 0;
		float pix0 = 0;
		float pix1 = 0;
		float pix2 = 0;

		for (int x = 0; x < img.xSize(); ++x) {
			pix2 = pix1;
			pix1 = pix0;
			pix0 = img(x, y) / 255.f;

			f2 = f1;
			f1 = f0;

			f0 = f(pix0, pix1, f1, f2);
			img_F(x, y) = f0;
		}

		// G --> from right to left
		float g0 = 0;
		float g1 = 0;
		float g2 = 0;
		pix0 = 0;
		pix1 = 0;
		pix2 = 0;
		for (int x = img.xSize() - 1; x >= 0; --x) {
			pix2 = pix1;
			pix1 = pix0;
			pix0 = img(x, y) / 255.f;

			g2 = g1;
			g1 = g0;

			g0 = g(pix1, pix2, g1, g2);
			img_G(x, y) = g0;
		}

		//sum both Filters
		for (int x = 0; x < img.xSize(); ++x) {
			img_result(x, y) = (img_F(x, y) + img_G(x, y)) * 255;
		}
	}

	
	for (int x = 0; x < img.xSize(); ++x) {
			// F --> from up to down
			float f0 = 0;
			float f1 = 0;
			float f2 = 0;
			float pix0 = 0;
			float pix1 = 0;
			float pix2 = 0;

			for (int y = 0; y < img.xSize(); ++y) {
				pix2 = pix1;
				pix1 = pix0;
				pix0 = img_result(x, y) / 255.f;

				f2 = f1;
				f1 = f0;

				f0 = f(pix0, pix1, f1, f2);
				img_F(x, y) = f0;
			}

			// G --> from down to up
			float g0 = 0;
			float g1 = 0;
			float g2 = 0;
			pix0 = 0;
			pix1 = 0;
			pix2 = 0;
			for (int y = img.ySize() - 1; y >= 0; --y) {
				pix2 = pix1;
				pix1 = pix0;
				pix0 = img_result(x, y) / 255.f;

				g2 = g1;
				g1 = g0;

				g0 = g(pix1, pix2, g1, g2);
				img_G(x, y) = g0;
			}

			//sum both Filters
			for (int y = 0; y < img.ySize(); ++y) {
				img_result(x, y) = (img_F(x, y) + img_G(x, y)) * 255;
			}
		}
	
	
	return img_result;
}

int mainFilterRecursive() {
	CMatrix<float> originImage, noisyImage;
	originImage.readFromPGM("lena.pgm");

	noisyImage = filterRecursive(originImage, 0.5f);

	noisyImage.writeToPGM("small-recursive.pgm");

	cout << "Recursive Filter finished" << endl;

	return 0;
}
