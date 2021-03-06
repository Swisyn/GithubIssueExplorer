package com.cuneytayyildiz.githubissueexplorer.ui.main.adapter

import android.widget.Filter
import com.cuneytayyildiz.githubissueexplorer.data.model.Issue


class MainListFilter(
        private var originalList: MutableList<Issue>,
        private var callback: IssueFilterCallback
) : Filter() {

    override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
        val filteredResults = getFilteredResults(constraint)
        val results = Filter.FilterResults()
        results.values = filteredResults
        return results
    }

    @Suppress("UNCHECKED_CAST")
    override fun publishResults(charSequence: CharSequence, results: Filter.FilterResults) {
        val filteredResults = (results.values as List<Issue>).toMutableList()
        callback.publishResults(filteredResults)
    }

    private fun getFilteredResults(constraint: CharSequence): List<Issue> {
        if (constraint.isEmpty()) {
            return originalList
        }
        val lowercaseConstraint = constraint.toString().toLowerCase()
        return originalList.filter {
            val titleMatch = it.title!!.toLowerCase().contains(lowercaseConstraint)
            val bodyMatch = it.body!!.toLowerCase().contains(lowercaseConstraint)
            return@filter titleMatch || bodyMatch
        }
    }
}

interface IssueFilterCallback {
    fun publishResults(filteredList: MutableList<Issue>)
}