/**
 * University of Freiburg.
 * This code has been provided within the scope of
 * the lecture Data Analysis and Query Languages
 * (Summer term 2015)
 * 
 * The following code is based on the class
 * org.recommender101.recommender.baseline.NearestNeighbors.java
 */
package daql.programmingtask5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.recommender101.data.DataModel;
import org.recommender101.data.DefaultDataLoader;
import org.recommender101.data.Rating;
import org.recommender101.gui.annotations.R101Class;
import org.recommender101.gui.annotations.R101Setting;
import org.recommender101.gui.annotations.R101Setting.SettingsType;
import org.recommender101.recommender.AbstractRecommender;
import org.recommender101.tools.Debug;
import org.recommender101.tools.Utilities101;

/**
 * Implements a standard user-based collaborative filtering approach. It uses a nearest neighbor method (user/user) with
 * Pearson correlation or Cosine Similarity as a metric
 * @author ...write your name here...
 *
 */
@R101Class (name="User-based collaborative filtering", description="It uses nearest neighbor method (user/user) with Pearson correlation or Cosine Similarity as a metric")
public class UserBasedCollaborativeFiltering extends AbstractRecommender {

	
	public Map<Integer, Float> averages;
	
	// Number of neighbors to consider (default 30)
	int nbNeighbors = 30;
	
	// The minimum similarity threshold
	double simThreshold = 0.0;
	

	// Some minimum overlap (co-rated items)
	int minRatingOverlap = 3;
	
	/**
	 * As a default we will use Pearson similarity
	 */
	boolean useCosineSimilarity = false;
	
	/**
	 * The minimum number of neighbors we need
	 */
	int minNeighbors = 1;
	
	/**
	 * Stores the similarities user-id-> map of other users and their similarities
	 */ 
	public Map<Integer, Map<Integer, Double>> theSimilarities = new HashMap<Integer, Map<Integer, Double>>();


	public Map<Integer, Set<Rating>> ratingsPerItem;
	
	
	/**
	 * Predict the rating based on the neighbors opinions.
	 * Use a classical weighting scheme and n neighbors
	 */
	@Override
	public synchronized float predictRating(int user, int item) {
//		System.out.println("predicting with neighbor data " + theSimilarities.keySet().size());
		
		// Iterate over all users and rank them
		// A map of similarities
		
		Map<Integer, Double> similarities = this.theSimilarities.get(user);
		
		// Check if we have enough neighbors
		if (similarities == null || (similarities.size() < this.minNeighbors)) {
//			System.out.println("oops no neighbor sfor " + user);
			return Float.NaN;
		}
		// The prediction function.
		// Take the user's average and add the weighted deviation of the neighbors.
		double totalSimilarity = 0;
		
		Float userAverage;
		
		userAverage = averages.get(user);
		if (userAverage == null) {
//			System.err.println("NearestNeighbors: There's no average rating for user " + item);
			return Float.NaN;
		}
		
		// go through all the neighbors 
		int cnt = 0;
		double totalBias = 0;
		//for each neighbor in the neighborhood
		for (Integer neighbor : similarities.keySet()) {
			float neighborRating = dataModel.getRating(neighbor, item);
			
			if (!Float.isNaN(neighborRating) && neighborRating != -1) {
				float neighborBias = neighborRating - averages.get(neighbor); 
				neighborBias = (float) (neighborBias * similarities.get(neighbor));
				totalBias += neighborBias;
				totalSimilarity += Math.abs(similarities.get(neighbor));
				// enough neighbors
				cnt++;
				if (cnt >= this.nbNeighbors) {
					break;
				}
			}
		}
		
		double result = userAverage + (totalBias / totalSimilarity);
		return (float) result;
	}

	// =====================================================================================
	/**
	 * Returns the set of ratings of a given item id 
	 * @param itemid the itemid
	 * @return the ratings, or null if no ratings exist
	 */
	public Set<Rating> getRatingsPerItem(Integer itemid) {
		return ratingsPerItem.get(itemid);
	}

	// =====================================================================================

	/**
	 * Pre-calculates the ratings per item from the data model
	 * @return the map of item-ids to ratings
	 */
	public Map<Integer, Set<Rating>> calculateRatingsPerItem() {
		Map<Integer, Set<Rating>> result = new HashMap<Integer, Set<Rating>>();
		
		Set<Rating> ratings = dataModel.getRatings();
		
		Set<Rating> ratingsOfItem;
		for (Rating r : ratings) {
			ratingsOfItem = result.get(r.item);
			if (ratingsOfItem == null) {
				ratingsOfItem = new HashSet<Rating>();
				result.put(r.item,ratingsOfItem);
			}
			ratingsOfItem.add(r);
		}
		return result;
	}

	// =====================================================================================
	
	/**
	 * This method recommends items.
	 */
	@Override
	public List<Integer> recommendItems(int user) {
		Debug.log("UserBasedKnn: Recommending items for : " + user);
		// Use the standard method based on the rating prediction
		return recommendItemsByRatingPrediction(user);
	}

	/**
	 * Initialization: Compute the user averages
	 */
	@Override
	public void init() throws Exception {
		computeAverages();
//		System.out.println(averages);
//		this.similarities = new HashMap<String, Double>();
		// Pre-compute the similarities between all users in the test set
		// Pre-compute the similarities between all users first
		double sim = Double.NaN;
		
		int similaritiesToCompute; 
		List<Integer> users;
		similaritiesToCompute = (dataModel.getUsers().size() * dataModel.getUsers().size())/2; 
		users = new ArrayList<Integer>(dataModel.getUsers());
		
		Debug.log("NearestNeighbors: Calculating up to " + similaritiesToCompute + " similarities in the test set.. This may take some time.");
	
		long start = System.currentTimeMillis();
		
		// sort in ascending order
		Collections.sort(users);
		Integer[] userArr = (Integer[]) users.toArray(new Integer[users.size()]);
		int counter = 0;
		int tenpercent = similaritiesToCompute / 10;
		for (int i=0;i<userArr.length;i++) {
			for (int j=i+1;j<userArr.length;j++) {
				counter++;
				if (counter % tenpercent == 0) {
					Debug.log("Similarity computation at : " + Math.round(((counter  / (double) similaritiesToCompute * 100))) + " %");
				}
				if (userArr[i] != null && userArr[j] != null) { // both must exist...
					sim = similarity(userArr[i], userArr[j]);
					if (!Double.isNaN(sim)) {
						if (sim > simThreshold) {
//						System.out.println("Putting: " + userArr[i] + ":" + userArr[j] + ":" + sim);
//							this.similarities.put(userArr[i] + ":" + userArr[j], sim);
							
							Map<Integer, Double> objectSimilarites1 = theSimilarities.get(userArr[i]);
							if (objectSimilarites1 == null) {
								objectSimilarites1 = new HashMap<Integer, Double>();
								theSimilarities.put(userArr[i], objectSimilarites1);
							}
							objectSimilarites1.put(userArr[j], sim);
							
							// Copy things
							Map<Integer, Double> objectSimilarites2 = theSimilarities.get(userArr[j]);
							
							if (objectSimilarites2 == null) {
								objectSimilarites2 = new HashMap<Integer, Double>();
								theSimilarities.put(userArr[j], objectSimilarites2);
							}
							objectSimilarites2.put(userArr[i], sim);
						}
					}
				}
			}
		}
		
		// go through all the user similarities and sort and prune them.
		for (Integer user : theSimilarities.keySet()) {
			Map<Integer, Double> sims = theSimilarities.get(user);
			sims = Utilities101.sortByValueDescending(sims);
			if (sims.size() > this.nbNeighbors) {
				Map<Integer, Double> copiedSims = new  LinkedHashMap<Integer, Double>();
				int cnt = 0;
				for (Integer simEntry : sims.keySet() ){
					copiedSims.put(simEntry, sims.get(simEntry));
					cnt++;
					if (cnt >= this.nbNeighbors) {
						break;
					}
				}
				sims = copiedSims;
			}
		}
		
		Debug.log("Nearest neighbors: Computed " + this.theSimilarities.size() + " similarities");
		Debug.log("Nearest neighbors: Time: " + (System.currentTimeMillis() - start) / 1000 + " secs");
	}

	/**
	 * Compute the average rating values
	 */
	protected void computeAverages() {
		averages = dataModel.getUserAverageRatings();
	}
	
	
	/**
	 * Calculates the Pearson or cosine similarity for two objects. Returns Double.NaN if
	 * there are not enough co-rated items
	 * @param object1 the first object
	 * @param object2 the second object
	 * @return a similarity value between -1 and 1
	 */
	double similarity (Integer object1, Integer object2) {
//		System.out.println("Calculating similarity for " + user1 + " and " + user2);
		// Get the ratings of the users 
		
		if (object1 == null || object2 == null) {
			System.out.println("---- No objects provided for similarity calculation");
			return Double.NaN;
		}
		
		// Ratings to compare
		Set<Rating> ratings1 = null;
		Set<Rating> ratings2 = null;
		
		ratings1 = dataModel.getRatingsPerUser().get(object1);
		ratings2 = dataModel.getRatingsPerUser().get(object2);
		
		if (ratings1 == null || ratings2 == null) {
//			System.out.println("issue here.. "+ object1 + " " + object2);
			return Double.NaN;
		}
		
		
		// Determine the ids of the co-rated items or the co-rating users in case of the item-based
		// approach
		Set<Integer> r1 = new HashSet<Integer>();
		Set<Integer> r2 = new HashSet<Integer>();
		
		for (Rating r : ratings1) {
			r1.add(r.item);
		}
		for (Rating r : ratings2) {
			r2.add(r.item);
		}
		
		
		// Calculate the overlap (intersection)
		// was rating all r1 before
		r1.retainAll(r2);
		
		if (r1.size() == 0 || r1.size() < this.minRatingOverlap) {
//			System.out.println("Too small overlap");
			return Double.NaN;
		}
//		System.out.println("going ahead");
		// calculate the similarity / correlation
		// user-based: r1 are co-rated items
		return calculateSimilarity(object1, object2, r1);
	}
	
	
	/**
	 * An internal function (to be overwritten in a subclass) to calculate the Pearson correlation of two 
	 * users
	 * @param object1 id of user 1 
	 * @param object2 id of user 2 
	 * @param overlap the set of co-rated users (item-based) or co-rated items (user-based)
	 * @return returns the similarity value;
	 */
	protected double calculateSimilarity(Integer user1, Integer user2, Set<Integer> overlap) {
		double result = Double.NaN;
		
		// Cosine similarity computation (and not adjusted cosine)
		if (useCosineSimilarity) {
			int commonObjects = overlap.size();
			// get the ratings
			double[] ratings1 = new double[commonObjects];
			double[] ratings2 = new double[commonObjects];
			
			int i = 0;
			// copy the ratings into arrays
			for (Integer coRated : overlap) {

				ratings1[i] = dataModel.getRating(user1, coRated);
				ratings2[i] = dataModel.getRating(user2, coRated);
				i++;
			}
			result = Utilities101.dot( ratings1, ratings2) / ( Math.sqrt( Utilities101.dot( ratings1, ratings1 ) ) * Math.sqrt( Utilities101.dot( ratings2, ratings2 ) ) );
			return result;
		}
		// Use Pearson correlation
		else {
			double mean1 = Double.NaN;
			double mean2 = Double.NaN; 
			try {
				mean1 = averages.get(user1);
				mean2 = averages.get(user2);
			}
			catch (Exception e) {
				System.out.println("EXCEPTION");
				System.out.println(user1 + " / " + user2);
				System.out.println(averages);
				e.printStackTrace();
			}
			
			double rating1;
			double rating2;
			
			double numerator = 0.0;
			
			double squaredDev1 = 0.0;
			double squaredDev2 = 0.0;
			
			// Iterate through all and sum things up
			for (Integer coRated : overlap) {				
				rating1 = dataModel.getRating(user1, coRated);
				rating2 = dataModel.getRating(user2, coRated);
				
				numerator += (rating1 - mean1) * (rating2 - mean2);
				squaredDev1 += Math.pow((rating1 - mean1),2);
				squaredDev2 += Math.pow((rating2 - mean2),2);
			}
			result = numerator / (Math.sqrt(squaredDev1) * Math.sqrt(squaredDev2));
			return result;
		}
	}
	
	// =====================================================================================
	


	/**
	 * Setter for the factory
	 * @param n the max number of neighbors
	 */
	@R101Setting( displayName="Max Neighbors", description="The maximum number of neighbors",
			defaultValue="30", type=SettingsType.INTEGER, minValue=0)
	public void setNeighbors(String n) {
		this.nbNeighbors = Integer.parseInt(n);
	}
	
	/**
	 * Sets the similarity threshold
	 * @param s
	 */
	@R101Setting (displayName="Minimum Similarity", defaultValue="0.0", minValue=0, type=SettingsType.DOUBLE,
			description="Similarity Threshold")
	public void setMinSimilarity(String s) {
		this.simThreshold = Double.parseDouble(s);
	}
	
	/**
	 * Setter for the min overlap value
	 * @param overlap
	 */
	@R101Setting(displayName="Minimum overlap", description="Minimum overlap value",
			type=SettingsType.INTEGER, defaultValue="3", minValue=0)
	public void setMinOverlap(String overlap) {
		this.minRatingOverlap = Integer.parseInt(overlap);
	}

	/**
	 * Setter for the min number of neighbors
	 * @param min
	 */
	@R101Setting(displayName="Min Neighbors", description="The minimum number of neighbors",
			defaultValue="1", type=SettingsType.INTEGER, minValue=0)
	public void setMinNeighbors(String min) {
		this.minNeighbors = Integer.parseInt(min);
	}
	

	/**
	 * Sets cosine similarity as the metric
	 * @param cosine should be "true"
	 */
	@R101Setting (displayName="Cosine similarity", type=SettingsType.BOOLEAN,
			defaultValue="false", description="Sets cosine similarity as the metric" )
	public void setCosineSimilarity(String cosine) {
		if ("true".equalsIgnoreCase(cosine)) {
			this.useCosineSimilarity = true;
		}
	}

	// Get the minimum value for the similarity metric to consider the neighbor
	public double getSimThreshold() {
		return simThreshold;
	}

	// Set the min value for the similarity measure to consider the neighbor
	public void setSimThreshold(String simThreshold) {
		try {
			this.simThreshold = Double.parseDouble(simThreshold);
		}
		catch (Exception e) {
			System.out.println("simThreshold has to be a double");
		}
	}
	
	// =====================================================================================
	
	public static void main(String[] args) {
		// Simple test method.
		System.out.println(" Testing kNN");
		try {
			DataModel dm = new DataModel();
			DefaultDataLoader loader = new DefaultDataLoader();
			loader.setMinNumberOfRatingsPerUser("250");
			loader.loadData(dm);
			UserBasedCollaborativeFiltering rec = new UserBasedCollaborativeFiltering();
			rec.setDataModel(dm);
			rec.setCosineSimilarity("false");
			rec.init();
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("-- kNN ended");
		

	}
	
	@Override
	public int getDurationEstimate() {
		return 8;
	}	
}
