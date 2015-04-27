#include "main.h"
#include "../lib/CMatrix.h"
#include <stdlib.h>
#include <math.h>

using namespace std;

float computeVarianzOrigin(CMatrix<float> img) {
	float var = 0;
	float avg = img.avg();
	float N = img.xSize() * img.ySize();

	for (int y = 0; y < img.ySize(); y++) {
		for (int x = 0; x < img.xSize(); x++) {
			//diff to avg for every pixel
			float diffAVG = img(x, y) - avg;
			var += pow(diffAVG, 2);
		}
	}

	var /= N;
	return var;
}

float computeVarianzNoise(CMatrix<float> origin, CMatrix<float> noise) {
	float var = 0;
	float N = origin.xSize() * origin.ySize();

	for (int y = 0; y < origin.ySize(); y++) {
		for (int x = 0; x < origin.xSize(); x++) {
			//diff to origin for every pixel
			float diffAVG = origin(x, y) - noise(x, y);
			var += pow(diffAVG, 2);
		}
	}

	var /= N;
	return var;
}

float computePSNR(CMatrix<float> origin, CMatrix<float> noise) {
	float var = 0;
	float N = origin.xSize() * origin.ySize();

	for (int y = 0; y < origin.ySize(); y++) {
		for (int x = 0; x < origin.xSize(); x++) {
			//diff to origin for every pixel
			float diffAVG = origin(x, y) - noise(x, y);
			var += pow(diffAVG, 2);
		}
	}

	float max = origin.max();
	float min = origin.min();

	if (var == 0)
		return INFINITY;
	else
		return 10 * log10((N * pow(max - min, 2)) / var);
}

int mainPSNR() {
	CMatrix<float> orignImage, noisyImage;

	orignImage.readFromPGM("lena.pgm");
	noisyImage = addGausNoise(orignImage, 255);
	noisyImage.writeToPGM("lena-noisy.pgm");

	float varOrigin = computeVarianzOrigin(orignImage);
	float varNoise = computeVarianzNoise(orignImage, noisyImage);
	float psnr = computePSNR(orignImage, noisyImage);

	cout << "Variance Origin: " << varOrigin << "  (" << sqrt(varOrigin) << ")"
			<< endl;
	cout << "Variance Noise:  " << varNoise << "  (" << sqrt(varNoise) << ")"
			<< endl;
	cout << "PSNR:            " << psnr << endl;

	return 0;
}

