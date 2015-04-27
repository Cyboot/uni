#include "main.h"
#include "../lib/CMatrix.h"
#include <stdlib.h>
#include <math.h>


using namespace std;

float randNormal() {
	return rand() / (float) RAND_MAX;
}

float boxMuller() {
	float u = randNormal();
	float v = randNormal();

	float n = sqrt(-2 * log2(u)) * cos(2 * M_PI * v);
	return n;
}

CMatrix<float> addGausNoise(CMatrix<float> matrix, int factor) {
	for (int y = 0; y < matrix.ySize(); y++) {
		for (int x = 0; x < matrix.xSize(); x++) {
			float value = matrix(x, y);
			value += (float) (boxMuller() * factor);

			if (value < 0)
				value = 0;
			if (value > 255)
				value = 255;

			matrix(x, y) = value;
		}
	}

	return matrix;
}

int mainNoise() {
	// Define image
	CMatrix<float> aImage;

	// Read image from a PGM file
	aImage.readFromPGM("lena.pgm");

	int size = 5;
	CMatrix<float> images[size];

	for (int i = 0; i < size; ++i) {
		images[i] = addGausNoise(aImage, 10);
		cout << "Avg: " << images[i].avg() << "  min: " << images[i].min()
				<< "  max: " << images[i].max() << endl;
	}

	// Write noisy image to PGM file
	aImage.writeToPGM("lena-noisy.pgm");
	return 0;
}

