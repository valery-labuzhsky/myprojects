package statref.model.fragment;

import statref.model.expressions.SExpression;
import statref.model.statements.SStatement;
import statref.model.types.SType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class ExpressionFragment implements SExpression {
    private final SExpression base;
    private final Set<Place<SExpression>> parts = new HashSet<>();

    public ExpressionFragment(SExpression base) {
        this.base = base;
    }

    public ExpressionFragment part(Place<SExpression> part) {
        parts.add(part);
        return this;
    }

    public SExpression getBase() {
        return base;
    }

    public Set<Place<SExpression>> getParts() {
        return parts;
    }

    @Override
    public SType getType() {
        return base.getType();
    }

    @Override
    public SStatement toStatement() {
        return base.toStatement();
    }

    @Override
    public Stream<Place<SExpression>> getExpressions() {
        return base.getExpressions().flatMap(place -> {
            if (parts.contains(place)) {
                return place.get(base).getExpressions().map(s -> new PlacementPlace(s, place));
            } else {
                return Stream.of(new BasePlace(place));
            }
        });
    }

    @Override
    public String toString() {
        return base.getText();
    }

    private static class PlacementPlace implements PlaceAdapter<ExpressionFragment, SExpression> {
        private final Place<SExpression> place;
        private final Place<SExpression> placement;

        public PlacementPlace(Place<SExpression> place, Place<SExpression> placement) {
            this.place = place;
            this.placement = placement;
        }

        @Override
        public String _getName(ExpressionFragment fragment) {
            return place.getName(getPlacement(fragment));
        }

        public SExpression getPlacement(ExpressionFragment fragment) {
            return placement.get(fragment.base);
        }

        @Override
        public SType _getType(ExpressionFragment fragment) {
            return place.getType(getPlacement(fragment));
        }

        @Override
        public SExpression _get(ExpressionFragment fragment) {
            return place.get(getPlacement(fragment));
        }

        @Override
        public void _set(ExpressionFragment fragment, SExpression value) {
            place.set(getPlacement(fragment), value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PlacementPlace that = (PlacementPlace) o;
            return place.equals(that.place) &&
                    placement.equals(that.placement);
        }

        @Override
        public int hashCode() {
            return Objects.hash(place, placement);
        }
    }

    private static class BasePlace implements PlaceAdapter<ExpressionFragment, SExpression> {
        private final Place<SExpression> place;

        private BasePlace(Place<SExpression> place) {
            this.place = place;
        }

        @Override
        public String _getName(ExpressionFragment fragment) {
            return place.getName(fragment.getBase());
        }

        @Override
        public SType _getType(ExpressionFragment fragment) {
            return place.getType(fragment.getBase());
        }

        @Override
        public SExpression _get(ExpressionFragment fragment) {
            return place.get(fragment.getBase());
        }

        @Override
        public void _set(ExpressionFragment fragment, SExpression value) {
            place.set(fragment.getBase(), value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BasePlace basePlace = (BasePlace) o;
            return place.equals(basePlace.place);
        }

        @Override
        public int hashCode() {
            return Objects.hash(place);
        }
    }

    @Override
    public Object getSignature() {
        return new Signature(this);
    }

    private static class Signature {
        private final Object signature;
        private final HashMap<Place<SExpression>, Object> signatures = new HashMap<>();

        Signature(ExpressionFragment fragment) {
            signature = fragment.base.getSignature();
            for (Place<SExpression> place : fragment.parts) {
                signatures.put(place, place.get(fragment.base).getSignature());
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Signature signature1 = (Signature) o;
            return signature.equals(signature1.signature) &&
                    signatures.equals(signature1.signatures);
        }

        @Override
        public int hashCode() {
            return Objects.hash(signature, signatures);
        }
    }
}
