/**
 * University of Freiburg.
 * This code has been provided within the scope of
 * the lecture Data Analysis and Query Languages
 * (Summer term 2015)
 */
package daql.programmingtask3;

import org.openrdf.model.Value;

/**
 * A class that defines a model for a feature, composed of a predicate and a
 * resource. This resource can be an object or a subject of an RDF triple.
 *
 */
public class Feature {
    /**
     * The class data members
     */
    private Value predicate;
    private Value resource;
    private FeatureType featureType;

    /**
     * Creates a new feature object
     * 
     * @param predicate
     * @param resource
     * @param featureType
     *            this should be of type FeatureType
     */
    public Feature(Value predicate, Value resource, FeatureType featureType) {
        this.predicate = predicate;
        this.resource = resource;
        this.featureType = featureType;
    }

    public Value getPredicate() {
        return predicate;
    }

    public void setPredicate(Value predicate) {
        this.predicate = predicate;
    }

    public Value getResource() {
        return resource;
    }

    public void setResource(Value resource) {
        this.resource = resource;
    }

    public FeatureType getFeatureType() {
        return featureType;
    }

    public void setFeatureType(FeatureType featureType) {
        this.featureType = featureType;
    }

    @Override
    public String toString() {
        return "Feature [predicate=" + predicate.stringValue() + ", object="
                        + resource.stringValue() + ", type=" + featureType
                        + "]";
    }

    @Override
    public int hashCode() {
        // TODO Generate a hash code out of the two values, multiply by
        return this.predicate.hashCode() * this.resource.hashCode()
                        * this.featureType.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        Feature f2 = (Feature) obj;
        return (this.predicate.equals(f2.getPredicate())
                        && this.resource.equals(f2.getResource()) && this.featureType
                            .equals((f2.getFeatureType())));
    }

}
