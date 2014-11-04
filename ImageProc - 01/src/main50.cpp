#include "main.h"

const int ARRAYSIZE = 50;

using namespace std;

CMatrix<float> averageImage(CMatrix<float> array[]) {
	CMatrix<float> avgImage = array[0];

	for (int y = 0; y < avgImage.ySize(); y++) {
		for (int x = 0; x < avgImage.xSize(); x++) {
			float value = 0;
			for (int i = 0; i < ARRAYSIZE; ++i) {
				value += array[i](x,y);
			}
			avgImage(x,y) = value / ARRAYSIZE;
		}
	}

	return avgImage;
}

int main50Img() {
	CMatrix<float> orignImage;
	orignImage.readFromPGM("lena.pgm");

	CMatrix<float> array[ARRAYSIZE];
	for (int i = 0; i < ARRAYSIZE; ++i) {
		array[i] = addGausNoise(orignImage, 500);
		cout << "Gaus: " << i+1 << "/" << ARRAYSIZE << endl;
	}

	CMatrix<float> avgImage = averageImage(array);
	float psnr = computePSNR(orignImage,avgImage);

	cout << "PSNR over 50 img: " << psnr  << endl;

	array[1].writeToPGM("lena-noisy50.pgm");
	avgImage.writeToPGM("lena-avg.pgm");
}
