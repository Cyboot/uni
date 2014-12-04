#include "main.h"
#include <sys/time.h>

int main(int argc, char** args) {
	clock_t t1 = clock();

	//	mainDerive();
	mainLucasKanade();
//	mainDenoise();
//	mainHornSchunk();
//	mainSolution();

	clock_t t2 = clock();

	long millis = (t2 - t1) * (1000.0 / CLOCKS_PER_SEC);
	cout << "took " << millis << " ms" << endl;

	return 0;
}

// ######################################################################################
// #######################     Utility Funktionen     ###################################
// ######################################################################################

float gaus(float x, float sigma) {
	float base = 1 / (sigma * sqrt(2 * M_PI));
	float exponent = -x * x / (2 * sigma * sigma);

	return base * exp(exponent);
}

/** Neumann Boundary Condition in X */
int indexX(CMatrix<float> img, int indexX) {
	if (indexX < 0)
		indexX = -indexX - 1;
	if (indexX >= img.xSize())
		indexX = img.xSize() * 2 - indexX - 1;

	return indexX;
}

/** Neumann Boundary Condition in Y */
int indexY(CMatrix<float> img, int indexY) {
	if (indexY < 0)
		indexY = -indexY - 1;
	if (indexY >= img.ySize())
		indexY = img.ySize() * 2 - indexY - 1;

	return indexY;
}
