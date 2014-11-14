#include "main.h"

CMatrix<float> normalize(CMatrix<float> img) {
	float min = img.min();
	float max = img.max();
	float varianz = max - min;

	for (int x = 0; x < img.xSize(); ++x) {
		for (int y = 0; y < img.ySize(); ++y) {
			float pimg = img(x, y) - min;
			pimg = pimg / varianz * 255;
			img(x, y) = pimg;
		}
	}
	return img;
}

CMatrix<float> derivative(CMatrix<float> img) {
	CMatrix<float> ix(img.xSize(), img.ySize());
	CMatrix<float> iy(img.xSize(), img.ySize());
	NFilter::filter<float>(img, ix, CDerivative<float>(3), 1);
	NFilter::filter<float>(img, iy, 1, CDerivative<float>(3));

	for (int x = 0; x < ix.xSize(); ++x) {
		for (int y = 0; y < ix.ySize(); ++y) {
			ix(x, y) += iy(x, y);
		}
	}

	ix = normalize(ix);

	return ix;
}

int main(int argc, char** args) {
	CMatrix<float> img;
	img.readFromPGM("img/derv/derv-0.pgm");

	for (int i = 1; i <= 500; ++i) {
		cout << "Derive " << i << endl;
		img = derivative(img);

		char buf[30];
		sprintf(buf,"img/derv/derv-%03d.pgm",i);

		if(i % 25 == 0 || i < 5)
			img.writeToPGM(buf);
	}

	return 0;
}
