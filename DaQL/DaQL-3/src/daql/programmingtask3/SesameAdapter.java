/**
 * University of Freiburg.
 * This code has been provided within the scope of
 * the lecture Data Analysis and Query Languages
 * (Summer term 2015)
 */

package daql.programmingtask3;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryInterruptedException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sparql.SPARQLRepository;

/**
 * This class manages the connection to a SPARQL endpoint. It enables
 * initializing a connection, posting SPARQL queries and closing the connection.
 *
 */
public class SesameAdapter {

    /**
     * The repository object
     */
    private Repository repo;

    /**
     * The Conection object
     */
    private RepositoryConnection conn;
    /**
     * The address of the SPARQL endpoint
     */
    private String endpointUrl;

    /**
     * Creates and initializes a new repository connected to the given endpoint
     * 
     * @param endpointUrl
     *            the address of the SPARQL endpoint
     * @throws RepositoryException
     */
    public SesameAdapter(String endpointUrl) throws RepositoryException {
        this.endpointUrl = endpointUrl;
        this.repo = new SPARQLRepository(endpointUrl);
        repo.initialize();
        this.conn = repo.getConnection();
    }

    /**
     * Closes the connection and shuts down of repository. Call this method only
     * after you post all of your SPARQL queries.
     * 
     * @throws RepositoryException
     */
    public void closeConnection() throws RepositoryException {
        this.conn.close();
        this.repo.shutDown();
    }

    /**
     * This function retrieves a list of incoming features of a given RDF
     * resource
     * 
     * @param object
     *            The RDF resource for which the incoming features are retrieved
     * @return a list of features
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    public Set<Feature> getIncommingFeatures(String object)
            throws RepositoryException {
        Set<Feature> result = new HashSet<Feature>();
        ValueFactory vf = this.repo.getValueFactory();

        URI obj = vf.createURI(object);
        RepositoryResult<Statement> statements = this.conn.getStatements(null,
                null, obj, false);
        try {
            while (statements.hasNext()) {
                Statement st = statements.next();
                Feature feat = new Feature(st.getPredicate(), st.getSubject(),
                        FeatureType.INCOMING);
                result.add(feat);
            }
        } catch (Exception ex) {
            System.out.println("exception :" + object + ", retrieved: "
                    + result.size() + " => " + ex);
        } finally {
            statements.close(); // make sure the result object is closed
                                // properly
        }
        return result;
    }

    /**
     * This function retrieves a list of outgoing features of a given RDF
     * resource
     * 
     * @param object
     *            The RDF resource for which the outgoing features are retrieved
     * @return a list of features
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    public Set<Feature> getOutgoingFeatures(String subject)
            throws RepositoryException, MalformedQueryException,
            QueryEvaluationException {
        Set<Feature> result = new HashSet<Feature>();
        ValueFactory vf = this.repo.getValueFactory();

        URI subj = vf.createURI(subject);
        RepositoryResult<Statement> statements = this.conn.getStatements(subj,
                null, null, false);
        try {
            while (statements.hasNext()) {
                Statement st = statements.next();
                Feature feat = new Feature(st.getPredicate(), st.getSubject(),
                        FeatureType.OUTGOING);
                result.add(feat);
            }
        } catch (Exception ex) {
            System.out.println("exception :" + subj + ", retrieved: "
                    + result.size());
        } finally {
            statements.close(); // make sure the result object is closed
                                // properly
        }
        return result;
    }

    /**
     * This function encapsulate a SPARQL query that retrieves for a given
     * feature, the number number of occurrences the feature appears in the
     * dataset.
     * 
     * @param predicate
     * @return
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    public int getPredicateFrequency(Feature feature)
            throws RepositoryException, MalformedQueryException,
            QueryEvaluationException {
        try {
            ValueFactory vf = this.repo.getValueFactory();
            String queryString = "SELECT (count(*) as ?count) where {?s ?p ?o .}";
            TupleQuery tupleQuery = this.conn.prepareTupleQuery(
                    QueryLanguage.SPARQL, queryString);
            switch (feature.getFeatureType()) {
            case INCOMING:
                tupleQuery.setBinding("s", feature.getResource());
                tupleQuery.setBinding("p", feature.getPredicate());
                break;
            case OUTGOING:
                tupleQuery.setBinding("o", feature.getResource());
                tupleQuery.setBinding("p", feature.getPredicate());
                break;
            }

            TupleQueryResult tResult = tupleQuery.evaluate();
            try {
                if (tResult.hasNext()) { // iterate over the result
                    BindingSet bindingSet = tResult.next();
                    int count = Integer.parseInt(bindingSet.getValue("count")
                            .stringValue());
                    return count;
                }
            } finally {
                tResult.close();
            }
        } catch (QueryInterruptedException ex) {
            // This exception is raised when a malformed URI appears in the data
            // set.
            // In this case we discard the feature and don't return a frequency.
            return -1;
        }
        return -1;
    }

}
