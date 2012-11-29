//ruleset {
//
//    description 'Gradle plugins CodeNarc RuleSet'
//
//   ruleset( "http://codenarc.sourceforge.net/StarterRuleSet-AllRulesByCategory.groovy.txt" ) {
//
//        DuplicateNumberLiteral   ( enabled : false )
//        DuplicateStringLiteral   ( enabled : false )
//        BracesForClass           ( enabled : false )
//        BracesForMethod          ( enabled : false )
//        BracesForIfElse          ( enabled : false )
//        BracesForForLoop         ( enabled : false )
//        BracesForTryCatchFinally ( enabled : false )
//        ThrowRuntimeException    ( enabled : false )
//		GrailsPublicControllerMethod (enabled : false)
//
//        AbcComplexity            ( maxMethodComplexity : 50  )
//        LineLength               ( length              : 180 )
//        MethodName               ( regex               : /[a-z][\w\s'\(\)]*/ ) // Spock method names
//
//		CyclomaticComplexity (maxMethodComplexity : 10)
//		MethodSize (maxLines : 40)
//		NestedBlockDepthRule (maxNestedBlockDepth : 3)
//    }
//

ruleset {
	description 'A custom Groovy RuleSet'

	CyclomaticComplexity { maxMethodComplexity = 10 }

	LineLength{  length = 180		 }
	AbcComplexity  {  maxMethodComplexity = 50		 }

	MethodSize{ maxLines = 40}

	ClassName

	MethodName
	
	ConfusingTernary(priority:3)
	
	NestedBlockDepth {maxNestedBlockDepth = 3}
}