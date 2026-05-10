#!/usr/bin/env python3
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BANK = ROOT / "app" / "src" / "main" / "assets" / "questions"
REQUIRED = {
    "id", "topic", "subtopic", "difficulty", "yearStyle", "stem", "options",
    "correctAnswerIndex", "conciseExplanation", "highYieldTakeaway", "tags"
}

index = json.loads((BANK / "index.json").read_text())
assert len(index) == 22, f"expected 22 topic files, found {len(index)}"
ids: set[str] = set()
for file_name in index:
    payload = json.loads((BANK / file_name).read_text())
    for question in payload["questions"]:
        missing = REQUIRED - question.keys()
        assert not missing, f"{question.get('id', file_name)} missing {missing}"
        assert len(question["options"]) == 5, question["id"]
        assert 0 <= question["correctAnswerIndex"] <= 4, question["id"]
        assert question["id"] not in ids, question["id"]
        ids.add(question["id"])
assert len(ids) == 2500, f"expected 2500 questions, found {len(ids)}"
print(f"Validated {len(ids)} questions across {len(index)} topic files.")
