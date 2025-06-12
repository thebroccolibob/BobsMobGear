package io.github.thebroccolibob.bobsmobgear.duck

import io.github.thebroccolibob.bobsmobgear.entity.WebShotEntity

var WebShotUser.webShot: WebShotEntity?
    get() = `bobsmobgear$getWebShot`()
    set(value) {
        `bobsmobgear$setWebShot`(value)
    }