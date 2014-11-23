#include "main.h"

void mainHornSchunk() {
	CMatrix<float> img0,img1;
	img0.readFromPGM("img/cropped-street_000009.pgm");
	img1.readFromPGM("img/cropped-street_000010.pgm");

	float alpha = 0.5;

	cout << "Horn-Schunk finished" << endl;
}
