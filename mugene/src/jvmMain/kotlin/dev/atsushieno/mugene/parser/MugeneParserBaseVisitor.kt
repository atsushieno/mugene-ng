// Generated from src/antlr/MugeneParser.g4 by ANTLR 4.7.2
package dev.atsushieno.mugene.parser

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor
import dev.atsushieno.mugene.parser.MugeneParserVisitor
import dev.atsushieno.mugene.parser.MugeneParser.ExpressionOrOperationUsesContext
import dev.atsushieno.mugene.parser.MugeneParser.OperationUsesContext
import dev.atsushieno.mugene.parser.MugeneParser.OperationUseContext
import dev.atsushieno.mugene.parser.MugeneParser.ArgumentsOptCurlyContext
import dev.atsushieno.mugene.parser.MugeneParser.ArgumentsContext
import dev.atsushieno.mugene.parser.MugeneParser.ArgumentContext
import dev.atsushieno.mugene.parser.MugeneParser.ConditionalExprContext
import dev.atsushieno.mugene.parser.MugeneParser.ComparisonExprContext
import dev.atsushieno.mugene.parser.MugeneParser.ComparisonOperatorContext
import dev.atsushieno.mugene.parser.MugeneParser.AddSubExprContext
import dev.atsushieno.mugene.parser.MugeneParser.MulDivModExprContext
import dev.atsushieno.mugene.parser.MugeneParser.PrimaryExprContext
import dev.atsushieno.mugene.parser.MugeneParser.UnaryExprContext
import dev.atsushieno.mugene.parser.MugeneParser.VariableReferenceContext
import dev.atsushieno.mugene.parser.MugeneParser.StringConstantContext
import dev.atsushieno.mugene.parser.MugeneParser.StepConstantContext
import dev.atsushieno.mugene.parser.MugeneParser.NumberOrLengthConstantContext
import dev.atsushieno.mugene.parser.MugeneParser.DotsContext
import dev.atsushieno.mugene.parser.MugeneParser.CanBeIdentifierContext
import dev.atsushieno.mugene.parser.MugeneParser.CommasContext

/**
 * This class provides an empty implementation of [MugeneParserVisitor],
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use [Void] for
 * operations with no return type.
</T> */
abstract class MugeneParserBaseVisitor<T> : AbstractParseTreeVisitor<T>(), MugeneParserVisitor<T> {
    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitExpressionOrOperationUses(ctx: ExpressionOrOperationUsesContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitOperationUses(ctx: OperationUsesContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitOperationUse(ctx: OperationUseContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitArgumentsOptCurly(ctx: ArgumentsOptCurlyContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitArguments(ctx: ArgumentsContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitArgument(ctx: ArgumentContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitExpression(ctx: MugeneParser.ExpressionContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitConditionalExpr(ctx: ConditionalExprContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitComparisonExpr(ctx: ComparisonExprContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitComparisonOperator(ctx: ComparisonOperatorContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitAddSubExpr(ctx: AddSubExprContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitMulDivModExpr(ctx: MulDivModExprContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitPrimaryExpr(ctx: PrimaryExprContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitUnaryExpr(ctx: UnaryExprContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitVariableReference(ctx: VariableReferenceContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitStringConstant(ctx: StringConstantContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitStepConstant(ctx: StepConstantContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitNumberOrLengthConstant(ctx: NumberOrLengthConstantContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitDots(ctx: DotsContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitCanBeIdentifier(ctx: CanBeIdentifierContext): T {
        return visitChildren(ctx)
    }

    /**
     * {@inheritDoc}
     *
     *
     * The default implementation returns the result of calling
     * [.visitChildren] on `ctx`.
     */
    override fun visitCommas(ctx: CommasContext): T {
        return visitChildren(ctx)
    }
}