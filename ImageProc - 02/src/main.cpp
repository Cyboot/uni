#include "main.h"


int main(int argc, char** args) {
//	mainDerive();
	mainLucasKanade();


	return 0;
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
