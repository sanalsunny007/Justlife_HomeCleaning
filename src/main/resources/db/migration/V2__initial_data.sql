INSERT INTO vehicle (name) VALUES
('Vehicle-1'), ('Vehicle-2'), ('Vehicle-3'), ('Vehicle-4'), ('Vehicle-5');

INSERT INTO cleaner (name, work_start, work_end, vehicle_id)
SELECT CONCAT('Cleaner-', v.id, '-', n.num), '08:00', '22:00', v.id
FROM vehicle v
CROSS JOIN (SELECT 1 num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) n;
