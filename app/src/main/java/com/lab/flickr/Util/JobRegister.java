package com.lab.flickr.Util;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matt on 26/05/2016.
 * This class provides a way to track the status for different tasks from multiple threads.
 * <p/>
 * The base value is a job. A job is a unit of work that consists of one or many tasks. Each task has a status
 * <p/>
 * A new Job is added and tasks are registered to that job with a status.
 * False for incomplete
 * True for complete
 */
public class JobRegister {

	public enum Job {
		MAIN_IMAGES,
	}

	private static volatile Map<Job, JobWrapper> jobs = new HashMap<>();

	/**
	 * Returns true if every task for a certain job is completed
	 *
	 * @param jobType
	 * @return
	 */
	public static synchronized boolean isAllTaskComplete(Job jobType) {
		JobWrapper job = jobs.get(jobType);
		if (job == null) {
			return true; //No job therefore all tasks are complete
		}
		Map<String, Boolean> tasks = job.getTasks();
		boolean completed = true;
		for (String taskId : tasks.keySet()) {
			completed = completed && tasks.get(taskId);
		}
		return completed;
	}

	/**
	 * Returns true if every task except the task for the ID provided is completed for a certain job
	 *
	 * @param jobType
	 * @param taskId
	 * @return
	 */
	public static synchronized boolean isAllOtherTaskComplete(Job jobType, String taskId) {
		JobWrapper job = jobs.get(jobType);
		if (job == null) {
			return true; //No job therefore all tasks are complete
		}
		Map<String, Boolean> tasks = job.getTasks();
		boolean completed = true;
		for (String id : tasks.keySet()) {
			if (!id.equals(taskId)) {
				completed = completed && tasks.get(id);
			}
		}
		return completed;
	}

	public static synchronized void addJob(Job jobType) {
		jobs.put(jobType, new JobWrapper());
	}

	/**
	 * Removes a job
	 *
	 * @param jobType
	 */
	public static synchronized void removeJob(Job jobType) {
		Log.d(JobRegister.class.getSimpleName(), "Removed called for job : " + jobType);
		jobs.remove(jobType);
	}

	/**
	 * Registers a task to a job or creates the job if it doesn't exist
	 *
	 * @param jobType
	 * @param taskId
	 */
	public static synchronized void registerTask(Job jobType, String taskId) {
		JobWrapper job = jobs.get(jobType);
		if (job == null) {
			addJob(jobType);
			job = jobs.get(jobType);
		}
		job.registerTask(taskId);
	}

	/**
	 * Registers a new task to a job or updates the value if this task already exists.
	 *
	 * @param jobType
	 * @param taskId
	 * @param state
	 */
	public static synchronized void updateTaskState(Job jobType, String taskId, boolean state) {
		JobWrapper job = jobs.get(jobType);
		job.registerTask(taskId, state);
	}

	private static class JobWrapper {
		private Map<String, Boolean> tasks = new HashMap<>();

		public Map<String, Boolean> getTasks() {
			return this.tasks;
		}

		public void registerTask(String taskId) {
			registerTask(taskId, false);
		}

		/**
		 * Adds a new task with a value or updates the existing one if it exists
		 *
		 * @param taskId
		 * @param state
		 */
		public void registerTask(String taskId, boolean state) {
			tasks.put(taskId, state);
		}
	}

}
