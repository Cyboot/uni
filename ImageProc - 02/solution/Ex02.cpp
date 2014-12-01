/**
 * Nikolaus Mayer, 2014 (mayern@cs.uni-freiburg.de)
 *
 * Image Processing and Computer Graphics
 * Winter Term 2014/2015
 * Exercise Sheet 2 (Image Processing part)
 */

/// STL/System
#include <fstream>
#include <iostream>
#include <stdexcept>
/// Local files
#include "../lib/CFilter.h"
#include "../lib/CMatrix.h"
#include "../lib/CTensor.h"
#include "flowToImage.h"
#include "HornSchunckMatrix.h"

// Element-wise product with another vector
template<typename T>
CVector<T> elem_mul(const CVector<T>& aVector, const CVector<T>& bVector) {
	CVector<T> result(aVector.size());
	for (int i = 0; i < aVector.size(); i++)
		result(i) = aVector(i) * bVector(i);
	return result;
}

/**
 * MAIN
 */
int mainSolution() {
	/// Setup ImageDisplay instance
	enum TASK {
		LUCAS_KANADE = 0, HORN_SCHUNK = 1
	};
	enum SOLVER {
		JACOBI = 0, GAUSS_SEIDEL, SOR, CG
	};

	/// Query user input
	int choice = -1;
	do {
		std::cout << "Choose, mortal...\n" << " 0: Lucas-Kanade\n" << " 1: Horn-Schunck\n"
				<< " Input: ";
		std::cin >> choice;
	} while (choice < 0 or choice > 1);

	/// Load images
	CMatrix<float> img0;
	CMatrix<float> img1;
	{
		int dataset = -1;
		do {
			std::cout << "Which dataset (0=street car, 1=yosemite): ";
			std::cin >> dataset;
		} while (dataset != 0 and dataset != 1);

		switch (dataset) {
		case 0: {
			img0.readFromPGM("img/cropped-street_000009.pgm");
			img1.readFromPGM("img/cropped-street_000010.pgm");
			break;
		}
		case 1: {
			img0.readFromPGM("img/yos08.pgm");
			img1.readFromPGM("img/yos09.pgm");
			break;
		}
		default:
			throw std::runtime_error("wat");
		}
	}

	/// /////////////////////////////////////////////////////////////////
	/// LUCAS-KANADE
	/// /////////////////////////////////////////////////////////////////

	if (choice == LUCAS_KANADE) {

		float sigma_presmoothing = -1;
		do {
			std::cout << "Choose presmoothing sigma (0 for no presmoothing): ";
			std::cin >> sigma_presmoothing;
		} while (sigma_presmoothing < 0);
		float sigma = -1;
		do {
			std::cout << "Choose sigma for K_rho (0 for no presmoothing): ";
			std::cin >> sigma;
		} while (sigma < 0);

		/// Gaussian filter
		CSmooth<float> gaussian2(sigma_presmoothing, 3.f);
		CSmooth<float> gaussian(sigma, 3.f);

		/// Derivative
		CDerivative<float> derivative(3);

		const int W = img0.xSize();
		const int H = img0.ySize();

		/// Pre-smooth input images
		if (sigma_presmoothing > 0) {
			NFilter::filter(img0, gaussian2, gaussian2);
			NFilter::filter(img1, gaussian2, gaussian2);
		}

		/// Compute derivatives
		CMatrix<float> Ix(img0);
		NFilter::filter(Ix, derivative, 1);
		CMatrix<float> Iy(img0);
		NFilter::filter(Iy, 1, derivative);
		CMatrix<float> It(img1);

		/// Compute matrix entries
		CMatrix<float> IxIx(Ix);
		CMatrix<float> IxIy(Ix);
		CMatrix<float> IxIt(Ix);
		CMatrix<float> IyIy(Iy);
		CMatrix<float> IyIt(Iy);
		for (int y = 0; y < H; ++y) {
			for (int x = 0; x < W; ++x) {
				It(x, y) -= img0(x, y);
				IxIx(x, y) *= Ix(x, y);
				IxIy(x, y) *= Iy(x, y);
				IxIt(x, y) *= It(x, y);
				IyIy(x, y) *= Iy(x, y);
				IyIt(x, y) *= It(x, y);
			}
		}
		if (sigma > 0) {
			NFilter::filter<float>(IxIx, gaussian, gaussian);
			NFilter::filter<float>(IxIy, gaussian, gaussian);
			NFilter::filter<float>(IxIt, gaussian, gaussian);
			NFilter::filter<float>(IyIy, gaussian, gaussian);
			NFilter::filter<float>(IyIt, gaussian, gaussian);
		}

		/// Solve Lucas-Kanade system of linear equations
		// ( A B )  ( u )  =  ( E )
		// ( C D )  ( v )  =  ( F )
		CTensor<float> LucasKanadeFlow(W, H, 2);
		float A, B, C, D, E, F, u, v;
		for (int y = 0; y < H; ++y) {
			for (int x = 0; x < W; ++x) {
				A = IxIx(x, y);
				B = IxIy(x, y);
				E = -1. * IxIt(x, y);
				C = B;
				D = IyIy(x, y);
				F = -1. * IyIt(x, y);
				u = (E * D - B * F) / (A * D - B * C);
				v = (F - C * u) / D;
				LucasKanadeFlow(x, y, 0) = u;
				LucasKanadeFlow(x, y, 1) = v;
			}
		}
		CTensor<float> LucasKanadeFlowRGB(W, H, 3);
		flowToImage(LucasKanadeFlow, LucasKanadeFlowRGB);

		/// Display stuff
		LucasKanadeFlowRGB.writeToPPM("Lucas-Kanade.ppm");
	}

	/// /////////////////////////////////////////////////////////////////
	/// HORN-SCHUNCK
	/// /////////////////////////////////////////////////////////////////

	else if (choice == HORN_SCHUNK) {

		/// Query user input
		int choice = -1;
		do {
			std::cout << std::endl << "Choose solver:\n" << " 0: Jacobi\n" << " 1: Gauss-Seidel\n"
					<< " 2: Successive Over-Relaxation (SOR)\n" << " 3: Conjugate Gradient (CG)\n"
					<< " Input: ";
			std::cin >> choice;
		} while (choice < 0 or choice > 3);

		switch (choice) {
		case JACOBI:
			std::cout << "Solving using Jacobi method...\n";
			break;
		case GAUSS_SEIDEL:
			std::cout << "Solving using Gauss-Seidel method...\n";
			break;
		case SOR:
			std::cout << "Solving using Successive Over-Relaxation...\n";
			break;
		case CG:
			std::cout << "Solving using Conjugate Gradients...\n";
			break;
		default:
			throw std::runtime_error("wat");
		}

		/// Pre-smooth input images
		{
			float sigma_presmoothing = -1;
			do {
				std::cout << "Choose presmoothing sigma (0 for no presmoothing): ";
				std::cin >> sigma_presmoothing;
			} while (sigma_presmoothing < 0);
			if (sigma_presmoothing > 0) {
				CSmooth<float> gaussian(sigma_presmoothing, 3.f);
				NFilter::filter(img0, gaussian, gaussian);
				NFilter::filter(img1, gaussian, gaussian);
			}
		}

		const int W = img0.xSize();
		const int H = img0.ySize();

		/// Derivative
		CDerivative<float> derivative(2);
		/// Compute derivatives
		CMatrix<float> Ix(img0);
		NFilter::filter(Ix, derivative, 1);
		CMatrix<float> Iy(img0);
		NFilter::filter(Iy, 1, derivative);
		CMatrix<float> It(img1);
		It -= img0;

		/// Let's try different methods for solving this...

		const float HORN_SCHUNCK_ALPHA = 200.f;

		HornSchunckMatrix hs(Ix, Iy, It, HORN_SCHUNCK_ALPHA);

		/// Jacobi, Gauss-Seidel, SOR
		if (choice == JACOBI or choice == GAUSS_SEIDEL or choice == SOR) {

			CVector<float> result(W * H * 2, 0.f);
			CVector<float> result_new(W * H * 2, 0.f);
			/// Luckily, we know exactly where the nonzero entries in the
			/// Horn-Schunck system are...
			std::vector<int> offsets;// = { 1, W, W * H };
			offsets.push_back(1);
			offsets.push_back(W);
			offsets.push_back(W*H);

			/// b in Ax=b
			CVector<float> b(W * H * 2);
			for (int i = 0; i < W * H * 2; ++i) {
				b(i) = hs.res(i);
			}

			/// D^-1 in x^{k+1} = D^-1 (b - Mx^k)
			CVector<float> Dinv(W * H * 2);
			for (int i = 0; i < W * H * 2; ++i) {
				Dinv(i) = 1. / hs(i, i);
			}

			/// Omega for SOR
			const float omega = ((choice == SOR) ? 1.9f : 1.f);

			/// Residual r in r=Ax-b
			CVector<float> residual(W * H * 2);

			const int ITERATIONS = 2000;
			const float CHANGE_THRESHOLD = 1e-3;
			const float RESIDUAL_THRESHOLD = 1e-3;

			std::cout << "Stopping conditions:" << " Change in the solution vector norm <"
					<< CHANGE_THRESHOLD << " or change in the residual <" << RESIDUAL_THRESHOLD
					<< " or maximum of " << ITERATIONS << " iterations reached." << std::endl;

			/// Keep track of termination criteria
			bool stop = false;

			for (size_t i = 0; i < ITERATIONS; ++i) {

				if (stop)
					break;

				float change_from_last_solution = 0.f;
				residual.fill(0.f);

				for (int _y = 0; _y < W * H * 2; ++_y) {
					float Mxk = 0.f;
					//for ( int _x = 0; _x < W*H*2; ++_x ) {  // <-- naive; VERY slow
					for (size_t idx = 0; idx < offsets.size(); ++idx) {  // <-- uses sparsity; fast
						switch (choice) {
						case JACOBI: {
							int _x = _y - offsets[idx];
							if (_x >= 0 and _x < W * H * 2)
								Mxk += hs(_x, _y) * result(_x);
							_x = _y + offsets[idx];
							if (_x >= 0 and _x < W * H * 2)
								Mxk += hs(_x, _y) * result(_x);
							break;
						}
						case GAUSS_SEIDEL:
						case SOR: {
							/// Upper triangle; use already computed new values
							int _x = _y - offsets[idx];
							if (_x >= 0 and _x < W * H * 2)
								Mxk += hs(_x, _y) * result_new(_x);
							/// Lower triangle; use old values
							_x = _y + offsets[idx];
							if (_x >= 0 and _x < W * H * 2)
								Mxk += hs(_x, _y) * result(_x);
							break;
						}
						default:
							throw std::runtime_error("wat");
						}
					}
					/// x^{k+1} = D^-1 (b - Mx^k)
					if (i > 0) {
						result_new(_y) = (1.f - omega) * result(_y)
								+ omega * Dinv(_y) * (b(_y) - Mxk);
					} else {
						result_new(_y) = Dinv(_y) * (b(_y) - Mxk);
					}
					change_from_last_solution += (result(_y) - result_new(_y))
							* (result(_y) - result_new(_y));

					residual(_y) = Mxk + hs(_y, _y) * result(_y) - b(_y);
				}

				if ((choice != JACOBI or i > 0)
						and (change_from_last_solution < CHANGE_THRESHOLD
								or residual.norm() < RESIDUAL_THRESHOLD))
					stop = true;

				/// Carry solution over into next iteration
				result = result_new;

				/// Info output
				if (i % 10 == 0 or i == ITERATIONS - 1 or stop) {
					std::cout << "Iteration " << i + 1 << "/" << ITERATIONS
							<< ": change in the solution = " << change_from_last_solution
							<< ", residual = " << residual.norm() << "      " << "\n";
				}

				/// Display intermediate result
				if (i % 50 == 0 or i == ITERATIONS - 1 or stop) {
					CTensor<float> mat_view(W, H, 2);
					int linear = 0;
					for (int y = 0; y < H; ++y) {
						for (int x = 0; x < W; ++x) {
							mat_view(x, y, 0) = result(linear);
							mat_view(x, y, 1) = result(linear + W * H);
							++linear;
						}
					}
					CTensor<float> rgb(W, H, 3);
					flowToImage(mat_view, rgb);
				}
			}
			std::cout << std::endl;
		}

		/// CG
		else {

			/// Use preconditioning?
			bool use_preconditioning = false;
			{
				char in;
				do {
					std::cout << "Use Jacobi preconditioner (y/n)? " << std::flush;
					std::cin >> in;
				} while (in != 'y' and in != 'n');
				if (in == 'y')
					use_preconditioning = true;
			}

			/// Initial solution is "zero everywhere"
			CVector<float> x(W * H * 2, 0.f);
			CVector<float> x_new(x);
			std::vector<int> offsets ;//= { -W * H, -W, -1, 0, 1, W, W * H };
			offsets.push_back(-W * H);
			offsets.push_back(-W);
			offsets.push_back(-1);
			offsets.push_back(0);
			offsets.push_back(1);
			offsets.push_back(W);
			offsets.push_back(W*H);

			/// b in Ax=b
			CVector<float> b(W * H * 2);
			for (int i = 0; i < W * H * 2; ++i) {
				b(i) = hs.res(i);
			}

			/// Residual r in r=Ax-b
			CVector<float> r(b);
			CVector<float> r_new(r);

			const int ITERATIONS = 2000;
			const float CHANGE_THRESHOLD = 1e-3;
			const float RESIDUAL_THRESHOLD = 1e-3;

			CVector<float> Dinv(W * H * 2);
			if (use_preconditioning)
				for (int i = 0; i < W * H * 2; ++i) {
					Dinv(i) = 1. / hs(i, i);
					b(i) *= Dinv(i);
				}

			std::cout << "Stopping conditions:" << " Change in the solution vector norm <"
					<< CHANGE_THRESHOLD << " or change in the residual <" << RESIDUAL_THRESHOLD
					<< " or maximum of " << ITERATIONS << " iterations reached."
					<< (use_preconditioning ? " Using Jacobi preconditioner." : "") << std::endl;

			bool stop = false;

			/// Let p0 be the residual p = r = b - A·x
			/// In our case, x={0..0} is the initial guess, so p0 = b
			CVector<float> p(b);

			/// A·p
			CVector<float> Ap(W * H * 2);

			float alpha, beta;

			for (size_t i = 0; i < ITERATIONS; ++i) {

				if (stop)
					break;

				/// A·p
				Ap.fill(0.f);
				for (int _y = 0; _y < W * H * 2; ++_y) {
					for (size_t idx = 0; idx < offsets.size(); ++idx) {
						int _x = _y + offsets[idx];
						if (_x >= 0 and _x < W * H * 2)
							Ap(_y) += hs(_x, _y) * p(_x);
					}
				}

				/// alpha = rT·r / pT·A·p
				if (use_preconditioning)
					alpha = (elem_mul(Dinv, r) * r) / (p * Ap);
				else
					alpha = (r * r) / (p * Ap);

				/// x' = x + alpha·p
				x_new = x + p * alpha;

				/// r' = r - alpha·A·p
				r_new = r - Ap * alpha;

				/// beta = rT'·r' / rT·r
				if (use_preconditioning)
					beta = (elem_mul(Dinv, r_new) * r_new) / (elem_mul(Dinv, r) * r);
				else
					beta = (r_new * r_new) / (r * r);

				/// p’ = r’ + beta·p
				if (use_preconditioning)
					p = elem_mul(Dinv, r_new) + p * beta;
				else
					p = r_new + p * beta;

				const float change_from_last_solution = (x_new - x).norm();
				if (i > 0
						and (change_from_last_solution < CHANGE_THRESHOLD
								or r.norm() < RESIDUAL_THRESHOLD))
					stop = true;

				/// Carry over to next iteration
				r = r_new;
				x = x_new;

				/// Info output
				if (i % 10 == 0 or i == ITERATIONS - 1 or stop) {
					std::cout << "Iteration " << i + 1 << "/" << ITERATIONS
							<< ": change in the solution = " << change_from_last_solution
							<< ", residual = " << r.norm() << "      " << "\n";
				}

				/// Display intermediate result
				if (i % 50 == 0 or i == ITERATIONS - 1 or stop) {
					CTensor<float> mat_view(W, H, 2);
					int linear = 0;
					for (int j = 0; j < H; ++j) {
						for (int i = 0; i < W; ++i) {
							mat_view(i, j, 0) = x(linear);
							mat_view(i, j, 1) = x(linear + W * H);
							++linear;
						}
					}
					CTensor<float> rgb(W, H, 3);
					flowToImage(mat_view, rgb);
				}
			}
			std::cout << std::endl;

		}

	}

	else {
		throw std::runtime_error("Invalid input");
	}

	/// Bye!
	return 0;
}

