INSERT INTO question_templates (
    id,
    artifact_type,
    question_text,
    difficulty,
    skill_tag,
    topic_tag,
    grade_level,
    is_active,
    created_at,
    updated_at
)
VALUES
-- Line graph questions
('11111111-1111-1111-1111-111111111101', 'line_graph', 'What overall trend is shown in the line graph?', 'easy', 'trend_interpretation', 'data_handling', 7, TRUE, NOW(), NOW()),
('11111111-1111-1111-1111-111111111102', 'line_graph', 'Which section of the line graph shows the greatest increase?', 'medium', 'rate_of_change', 'data_handling', 8, TRUE, NOW(), NOW()),
('11111111-1111-1111-1111-111111111103', 'line_graph', 'What can be inferred from the highest and lowest points on the graph?', 'medium', 'data_inference', 'data_handling', 8, TRUE, NOW(), NOW()),
('11111111-1111-1111-1111-111111111104', 'line_graph', 'How does the value change from the beginning to the end of the graph?', 'easy', 'comparison', 'data_handling', 7, TRUE, NOW(), NOW()),
('11111111-1111-1111-1111-111111111105', 'line_graph', 'What possible real-world explanation could account for the trend shown?', 'hard', 'real_world_reasoning', 'data_interpretation', 9, TRUE, NOW(), NOW()),

-- Bar graph questions
('22222222-2222-2222-2222-222222222201', 'bar_graph', 'Which category has the highest value in the bar graph?', 'easy', 'comparison', 'data_handling', 6, TRUE, NOW(), NOW()),
('22222222-2222-2222-2222-222222222202', 'bar_graph', 'Which category has the lowest value in the bar graph?', 'easy', 'comparison', 'data_handling', 6, TRUE, NOW(), NOW()),
('22222222-2222-2222-2222-222222222203', 'bar_graph', 'What is the difference between the highest and lowest categories?', 'medium', 'quantitative_comparison', 'data_handling', 7, TRUE, NOW(), NOW()),
('22222222-2222-2222-2222-222222222204', 'bar_graph', 'What pattern can you observe across the categories?', 'medium', 'pattern_recognition', 'data_interpretation', 8, TRUE, NOW(), NOW()),
('22222222-2222-2222-2222-222222222205', 'bar_graph', 'Which category would you investigate further and why?', 'hard', 'critical_thinking', 'data_interpretation', 9, TRUE, NOW(), NOW()),

-- Pie chart questions
('33333333-3333-3333-3333-333333333301', 'pie_chart', 'Which section represents the largest part of the whole?', 'easy', 'part_whole_relationship', 'data_handling', 6, TRUE, NOW(), NOW()),
('33333333-3333-3333-3333-333333333302', 'pie_chart', 'Which section represents the smallest part of the whole?', 'easy', 'part_whole_relationship', 'data_handling', 6, TRUE, NOW(), NOW()),
('33333333-3333-3333-3333-333333333303', 'pie_chart', 'What does the pie chart show about how the whole is divided?', 'medium', 'percentage_interpretation', 'data_handling', 7, TRUE, NOW(), NOW()),
('33333333-3333-3333-3333-333333333304', 'pie_chart', 'How would the chart change if one category increased?', 'medium', 'proportional_reasoning', 'data_interpretation', 8, TRUE, NOW(), NOW()),
('33333333-3333-3333-3333-333333333305', 'pie_chart', 'What conclusion can be drawn from the largest and smallest sections?', 'hard', 'data_inference', 'data_interpretation', 9, TRUE, NOW(), NOW()),

-- Unknown visual artifact questions
('44444444-4444-4444-4444-444444444401', 'unknown', 'What type of information does this visual artifact appear to communicate?', 'easy', 'visual_observation', 'artifact_analysis', 6, TRUE, NOW(), NOW()),
('44444444-4444-4444-4444-444444444402', 'unknown', 'What labels, symbols, or patterns can you identify in the visual artifact?', 'easy', 'visual_literacy', 'artifact_analysis', 6, TRUE, NOW(), NOW()),
('44444444-4444-4444-4444-444444444403', 'unknown', 'What question could be asked to better understand this visual artifact?', 'medium', 'question_generation', 'artifact_analysis', 7, TRUE, NOW(), NOW()),
('44444444-4444-4444-4444-444444444404', 'unknown', 'What additional information would help interpret this artifact accurately?', 'medium', 'critical_observation', 'artifact_analysis', 8, TRUE, NOW(), NOW()),
('44444444-4444-4444-4444-444444444405', 'unknown', 'What possible real-world context could this visual artifact represent?', 'hard', 'contextual_reasoning', 'artifact_analysis', 9, TRUE, NOW(), NOW());