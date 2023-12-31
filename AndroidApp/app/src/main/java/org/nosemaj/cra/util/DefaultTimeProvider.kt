package org.nosemaj.cra.util

import javax.inject.Inject
import kotlinx.datetime.Clock

class DefaultTimeProvider
@Inject
constructor() : TimeProvider {
    override fun now() = Clock.System.now()
}
