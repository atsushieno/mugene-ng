// Generated from src/antlr/MugeneParser.g4 by ANTLR 4.7.2
package dev.atsushieno.mugene.parser

import dev.atsushieno.mugene.parser.MugeneParser.*
import org.antlr.v4.runtime.tree.ParseTreeVisitor

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by [MugeneParser].
 *
 * @param <T> The return type of the visit operation. Use [Void] for
 * operations with no return type.
</T> */
interface MugeneParserVisitor<T> : ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by [MugeneParser.expressionOrOperationUses].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitExpressionOrOperationUses(ctx: ExpressionOrOperationUsesContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.operationUses].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitOperationUses(ctx: OperationUsesContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.operationUse].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitOperationUse(ctx: OperationUseContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.argumentsOptCurly].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitArgumentsOptCurly(ctx: ArgumentsOptCurlyContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.arguments].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitArguments(ctx: ArgumentsContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.argument].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitArgument(ctx: ArgumentContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.expression].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitExpression(ctx: MugeneParser.ExpressionContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.conditionalExpr].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitConditionalExpr(ctx: ConditionalExprContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.comparisonExpr].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitComparisonExpr(ctx: ComparisonExprContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.comparisonOperator].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitComparisonOperator(ctx: ComparisonOperatorContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.addSubExpr].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitAddSubExpr(ctx: AddSubExprContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.mulDivModExpr].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitMulDivModExpr(ctx: MulDivModExprContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.primaryExpr].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitPrimaryExpr(ctx: PrimaryExprContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.unaryExpr].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitUnaryExpr(ctx: UnaryExprContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.variableReference].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitVariableReference(ctx: VariableReferenceContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.stringConstant].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitStringConstant(ctx: StringConstantContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.stepConstant].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitStepConstant(ctx: StepConstantContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.numberOrLengthConstant].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitNumberOrLengthConstant(ctx: NumberOrLengthConstantContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.dots].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitDots(ctx: DotsContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.canBeIdentifier].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitCanBeIdentifier(ctx: CanBeIdentifierContext): T

    /**
     * Visit a parse tree produced by [MugeneParser.commas].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitCommas(ctx: CommasContext): T
}