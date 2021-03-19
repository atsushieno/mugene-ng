// Generated from src/antlr/MugeneParser.g4 by ANTLR 4.7.2
package dev.atsushieno.mugene.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MugeneParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MugeneParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MugeneParser#expressionOrOperationUses}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionOrOperationUses(MugeneParser.ExpressionOrOperationUsesContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#operationUses}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperationUses(MugeneParser.OperationUsesContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#operationUse}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperationUse(MugeneParser.OperationUseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#argumentsOptCurly}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgumentsOptCurly(MugeneParser.ArgumentsOptCurlyContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#arguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArguments(MugeneParser.ArgumentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#argument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgument(MugeneParser.ArgumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(MugeneParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#conditionalExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionalExpr(MugeneParser.ConditionalExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#comparisonExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonExpr(MugeneParser.ComparisonExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#comparisonOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonOperator(MugeneParser.ComparisonOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#addSubExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddSubExpr(MugeneParser.AddSubExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#mulDivModExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulDivModExpr(MugeneParser.MulDivModExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#primaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimaryExpr(MugeneParser.PrimaryExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#unaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpr(MugeneParser.UnaryExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#variableReference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableReference(MugeneParser.VariableReferenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#stringConstant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringConstant(MugeneParser.StringConstantContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#stepConstant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStepConstant(MugeneParser.StepConstantContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#numberOrLengthConstant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberOrLengthConstant(MugeneParser.NumberOrLengthConstantContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#dots}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDots(MugeneParser.DotsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#canBeIdentifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCanBeIdentifier(MugeneParser.CanBeIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link MugeneParser#commas}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommas(MugeneParser.CommasContext ctx);
}