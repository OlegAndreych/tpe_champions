package org.andreych.tpe_champions.estimation.exceptions

/**
 * Base application exception.
 * Created by oleg on 04.06.16.
 */
abstract class ChampionsEstimationException : RuntimeException
{
    constructor() : super()
    constructor(message: String) : super(message)
}

class ValidationEstimationException : ChampionsEstimationException
{
    constructor() : super()
    constructor(message: String) : super(message)
}

class NoTaskException : ChampionsEstimationException
{
    constructor() : super()
    constructor(message: String) : super(message)
}

class NoPerformerException : ChampionsEstimationException
{
    constructor() : super()
    constructor(message: String) : super(message)
}

class NoEstimationException : ChampionsEstimationException
{
    constructor() : super()
    constructor(message: String) : super(message)
}