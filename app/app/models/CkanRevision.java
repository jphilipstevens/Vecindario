package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.db.ebean.Model;

/**
 * This is the model for representing a ckan_revisions.
 * 
 * @author Matthew Weiler
 */
@Entity
@Table(name = "ckan_revisions", uniqueConstraints =
{ @UniqueConstraint(columnNames =
{ "dataset_id", "resource_id" }) })
public class CkanRevision extends Model
{
	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "dataset_id", nullable = false, length = 36)
	public String datasetId;
	
	@Column(name = "resource_id", nullable = false, length = 36)
	public String resourceId;
	
	@Column(name = "last_revision_time", nullable = false)
	public long lastRevisionTime;
	
	public static Finder<Integer, CkanRevision> find = new Finder<Integer, CkanRevision>(
			Integer.class, CkanRevision.class);
	
	/**
	 * This method will return the {@link CkanRevision},
	 * from the database, that matches the specified
	 * data-set id and resource id.
	 * 
	 * @param datasetId
	 * The data-set id.
	 * @param resourceId
	 * The resource id.
	 * 
	 * @return
	 * The {@link CkanRevision} that matches the specified fields.
	 * */
	public static CkanRevision getCkanRevision(final String datasetId, final String resourceId)
	{
		return CkanRevision.find.where().eq("datasetId", datasetId).eq("resourceId", resourceId).findUnique();
	}
	
}
