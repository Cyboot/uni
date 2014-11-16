#include "main.h"

CMatrix<float> deriveX(CMatrix<float> img0) {
	CMatrix<float> ix(img0.xSize(), img0.ySize());
	NFilter::filter<float>(img0, ix, CDerivative<float>(3), 1);

	return ix;
}

CMatrix<float> deriveY(CMatrix<float> img0) {
	CMatrix<float> iy(img0.xSize(), img0.ySize());
	NFilter::filter<float>(img0, iy, 1, CDerivative<float>(3));

	return iy;
}

CMatrix<float> deriveT(CMatrix<float> img0, CMatrix<float> img1) {
	CMatrix<float> result = CMatrix<float>(img0.xSize(), img0.ySize());

	for (int x = 0; x < img0.xSize(); ++x) {
		for (int y = 0; y < img0.ySize(); ++y) {
			result(x, y) = img1(x, y) - img0(x, y);
		}
	}
	return result;
}

CMatrix<float> lucasKanade(CMatrix<float> img0, CMatrix<float> img1) {
	CMatrix<float> resultX(img0.xSize(), img0.ySize());

	CMatrix<float> ix = deriveX(img0);
	CMatrix<float> iy = deriveY(img0);
	CMatrix<float> it = deriveT(img0, img1);

	for (int y = 0; y < img0.ySize(); ++y) {
		for (int x = 0; x < img0.xSize(); ++x) {
			float u = 0;

			float sum_xt = 0;
			float sum_yt = 0;
			float sum_xy = 0;
			float sum_y2 = 0;
			float sum_x2 = 0;

			int factor = 3;

			//up and down from Pixel
			for (int dy = -factor; dy <= factor; ++dy) {
				int yIndex = indexY(iy, y + dy);
				//left and right from Pixel
				for (int dx = -factor; dx <= factor; ++dx) {
					int xIndex = indexX(ix, x + dx);

					float derv_X = ix(xIndex, yIndex);
					float derv_Y = iy(xIndex, yIndex);
					float derv_T = it(xIndex, yIndex);

					sum_xt += derv_X * derv_T;
					sum_x2 += derv_X * derv_X;
					sum_yt += derv_Y * derv_T;
					sum_y2 += derv_Y * derv_Y;

					sum_xy += derv_X * derv_Y;
				}
			}

			sum_xt /= pow(2 * factor + 1, 2);
			sum_yt /= pow(2 * factor + 1, 2);
			sum_xy /= pow(2 * factor + 1, 2);
			sum_y2 /= pow(2 * factor + 1, 2);
			sum_x2 /= pow(2 * factor + 1, 2);

			// equation from Tutor 2
//			float zaehler = sum_xt + (sum_yt * sum_xt / sum_y2);
//			float nenner = (sum_xy * sum_xy / sum_y2) + sum_x2;
//			u = zaehler / nenner;

			// equation with inverse matrix
			float alpha = 1 / (sum_x2 * sum_y2 - sum_xy * sum_xy);
			float first = alpha * sum_y2 * sum_xt;
			float second = alpha * (-sum_xy) * sum_yt;
			u = first + second;

			if (isnan(u)) {
				u = 0;
			}

			u = -u;

			resultX(x, y) = u * 127;
//			cout << x << ":" << y << "  =>  " << u << endl;
		}
		cout << y << "/" << img0.ySize() << endl;
	}
	resultX = normalize(resultX);
	resultX.writeToPGM("img/moveX.pgm");

}

void mainLucasKanade() {
	CMatrix<float> img0, img1;
	img0.readFromPGM("img/img00.pgm");
	img1.readFromPGM("img/img01.pgm");
//	img0.readFromPGM("img/cropped-street_000009.pgm");
//	img1.readFromPGM("img/cropped-street_000010.pgm");

// Test: diffence between Images (in Time)
//	normalize(deriveT(img0,img1)).writeToPGM("img/diff.pgm");

	lucasKanade(img0, img1);

	cout << "Finished LucasKanade!" << endl;
}
