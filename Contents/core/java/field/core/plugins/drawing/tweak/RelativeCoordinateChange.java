package field.core.plugins.drawing.tweak;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import field.core.dispatch.iVisualElement;
import field.core.plugins.drawing.opengl.CachedLine;
import field.core.plugins.drawing.tweak.TweakSplineCodeGen.iCoordDesc;
import field.core.plugins.drawing.tweak.TweakSplineCodeGen.iResult;
import field.core.plugins.drawing.tweak.TweakSplineUI.MouseInfo;
import field.core.plugins.drawing.tweak.TweakSplineUI.SelectedVertex;
import field.core.plugins.drawing.tweak.TweakSplineUI.SubSelection;
import field.math.linalg.Vector2;


public class RelativeCoordinateChange implements iCoordDesc {

	public iResult describe(List<CachedLine> index, List<SelectedVertex> claimed, MouseInfo mi) {
		if (claimed.size() == 0) return null;

		final SelectedVertex c = claimed.remove(0);
		final Vector2[] changes = { null, null, null};
		if (c.frozenPosition.containsKey(SubSelection.postion) && c.absPosition.containsKey(SubSelection.postion)) {
			if (c.frozenPosition.get(SubSelection.postion).distanceFrom(c.absPosition.get(SubSelection.postion)) > 1e-4) {
				changes[1] = new Vector2(c.absPosition.get(SubSelection.postion)).sub(c.frozenPosition.get(SubSelection.postion));
			}
		}
		if (changes[1] == null) changes[1] = new Vector2(0, 0);

		if (c.frozenPosition.containsKey(SubSelection.previousControl) && c.absPosition.containsKey(SubSelection.previousControl)) {
			if (c.frozenPosition.get(SubSelection.previousControl).distanceFrom(c.absPosition.get(SubSelection.previousControl)) > 1e-4) {
				changes[0] = new Vector2(c.absPosition.get(SubSelection.previousControl)).sub(c.frozenPosition.get(SubSelection.previousControl)).sub(changes[1]);
			}
		}
		if (c.frozenPosition.containsKey(SubSelection.nextControl) && c.absPosition.containsKey(SubSelection.nextControl)) {
			if (c.frozenPosition.get(SubSelection.nextControl).distanceFrom(c.absPosition.get(SubSelection.nextControl)) > 1e-4) {
				changes[2] = new Vector2(c.absPosition.get(SubSelection.nextControl)).sub(c.frozenPosition.get(SubSelection.nextControl)).sub(changes[1]);
			}
		}

		if (changes[0] == null && changes[1] == null && changes[2] == null) return TweakSplineCodeGen.abort;

		return new iResult(){
			public List<SelectedVertex> getClaimedVertex() {
				return Collections.singletonList(c);
			}

			public String toExpression() {
				return "Rel(" + v(changes[0]) + ", " + v(changes[1]) + ", " + v(changes[2]) + ")";
			}

			protected String v(Vector2 vv) {
				return (vv == null ? "None" : "Vector2(" + vv.x + ", " + vv.y + ")");
			}

			public void toProperties(iVisualElement e, Map<String, Object> soFar) {
			}
		};
	}
}
