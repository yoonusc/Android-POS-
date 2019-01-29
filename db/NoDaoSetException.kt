package com.ionob.pos.db

/**
 * Exception of when trying to getInstance() without inject its DAO.
 *
 * @author Ionob Team
 */
class NoDaoSetException : Exception() {
    companion object {

        private val serialVersionUID = 1L
    }

}
