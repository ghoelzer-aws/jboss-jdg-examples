package org.jboss.infinispan.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.lucene.search.Query;
import org.hibernate.search.query.dsl.QueryBuilder;

//==== JDG Additions ===>
import org.infinispan.Cache;
import org.infinispan.query.CacheQuery;
import org.infinispan.query.Search;
import org.infinispan.query.SearchManager;
import org.jboss.infinispan.demo.model.Task;
//======================>

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TaskService {

	@PersistenceContext
    EntityManager em;


//==== JDG Additions ===>
	@Inject
	Cache<Long,Task> cache;
//======================>	
	Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * This methods should return all cache entries, RE-CODED for JDG. 
	 * @return
	 */
	public Collection<Task> findAll() {
		return cache.values();
	}
	
	/**
	 * This method filters task based on the input, RE-CODED for JDG
	 * @param input - string to filter on
	 * @return
	 * 
	 */
	public Collection<Task> filter(String input) {
		SearchManager sm = Search.getSearchManager(cache);
		QueryBuilder qb = sm.buildQueryBuilderForClass(Task.class).get();
		Query q = qb.keyword().onField("title").matching(input).createQuery();
		CacheQuery cq = sm.getQuery(q, Task.class);
		List<Task> tasks = new ArrayList<Task>();
		for (Object object : cq) {
			tasks.add((Task) object);
		}
		return tasks;
	}

	/**
	 * This method persists a new Task instance
	 * @param task
	 * 
	 */
	public void insert(Task task) {
		if(task.getCreatedOn()==null)
			task.setCreatedOn(new Date());
		em.persist(task);
	//==== JDG Additions ===>
		cache.put(task.getId(),task);
	//======================>
	}


	/**
	 * This method persists an existing Task instance
	 * @param task
	 * 
	 */
	public void update(Task task) {
		Task newTask = em.merge(task);
		em.detach(newTask);
	//==== JDG Additions ===>
		cache.replace(task.getId(),newTask);
	//======================>
	}
	
	/**
	 * This method deletes an Task from the persistence store
	 * @param task
	 * 
	 */
	public void delete(Task task) {
		//Note object may be detached so we need to tell it to remove based on reference
		em.remove(em.getReference(task.getClass(),task.getId()));
	//==== JDG Additions ===>
		cache.remove(task.getId());
	//======================>
	
	}
	
	
	/**
	 * This method is called after construction of this SLSB, ADDED to populate JDG
	 * 
	 */
	@PostConstruct
	public void startup() {
		
		log.info("### Querying the database for tasks!!!!");
		final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		final CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
	
		Root<Task> root = criteriaQuery.from(Task.class);
		criteriaQuery.select(root);
		Collection<Task> resultList = em.createQuery(criteriaQuery).getResultList();
		
		for (Task task : resultList) {
			this.insert(task);
		}
		
	}
	
}
