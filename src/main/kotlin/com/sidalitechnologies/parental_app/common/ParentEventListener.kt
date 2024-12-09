package com.sidalitechnologies.parental_app.common

import com.sidalitechnologies.parental_app.model.Parent
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ParentEventListener : AbstractMongoEventListener<Parent>() {
    override fun onBeforeConvert(event: BeforeConvertEvent<Parent>) {
        super.onBeforeConvert(event)
        val parent = event.source
        val now = LocalDateTime.now()

        if (parent.id == null) {
            parent.createdDate = now
        }

        // Always set lastModifiedDate
        parent.lastModifiedDate = now

        super.onBeforeConvert(event)

    }
}