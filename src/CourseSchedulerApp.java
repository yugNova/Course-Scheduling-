import java.time.DayOfWeek;
import java.time.LocalTime;


import java.util.*;

    // Core classes
    class Course {
        private String name;
        private int credits;
        private List<Course> prerequisites;
        private List<TimeSlot> availableTimeSlots;

        public Course(String name, int credits) {
            this.name = name;
            this.credits = credits;
            this.prerequisites = new ArrayList<>();
            this.availableTimeSlots = new ArrayList<>();
        }

        // Getters and setters
        public String getName() { return name; }
        public int getCredits() { return credits; }
        public List<Course> getPrerequisites() { return prerequisites; }
        public List<TimeSlot> getAvailableTimeSlots() { return availableTimeSlots; }

        public void addPrerequisite(Course course) {
            prerequisites.add(course);
        }

        public void addAvailableTimeSlot(TimeSlot timeSlot) {
            availableTimeSlots.add(timeSlot);
        }
    }

    class TimeSlot {
        private DayOfWeek day;
        private LocalTime startTime;
        private LocalTime endTime;

        public TimeSlot(DayOfWeek day, LocalTime startTime, LocalTime endTime) {
            this.day = day;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        // Getters
        public DayOfWeek getDay() { return day; }
        public LocalTime getStartTime() { return startTime; }
        public LocalTime getEndTime() { return endTime; }

        public boolean conflictsWith(TimeSlot other) {
            return this.day == other.day &&
                    !(this.endTime.isBefore(other.startTime) || other.endTime.isBefore(this.startTime));
        }
    }

    class Schedule {
        private Map<Course, TimeSlot> courseAssignments;

        public Schedule() {
            this.courseAssignments = new HashMap<>();
        }

        public void assignCourse(Course course, TimeSlot timeSlot) {
            courseAssignments.put(course, timeSlot);
        }

        public void removeCourse(Course course) {
            courseAssignments.remove(course);
        }

        public TimeSlot getAssignment(Course course) {
            return courseAssignments.get(course);
        }

        public Map<Course, TimeSlot> getAllAssignments() {
            return new HashMap<>(courseAssignments);
        }
    }

    // Main components
    class CourseManager {
        private List<Course> courses;

        public CourseManager() {
            this.courses = new ArrayList<>();
        }

        public void addCourse(Course course) {
            courses.add(course);
        }

        public void removeCourse(Course course) {
            courses.remove(course);
        }

        public List<Course> getCourses() {
            return new ArrayList<>(courses);
        }
    }

    class ConflictDetector {
        public boolean hasConflict(Schedule schedule, Course course, TimeSlot timeSlot) {
            // Check for time conflicts
            for (Map.Entry<Course, TimeSlot> entry : schedule.getAllAssignments().entrySet()) {
                if (entry.getValue().conflictsWith(timeSlot)) {
                    return true;
                }
            }

            // Check for prerequisite violations
            for (Course prerequisite : course.getPrerequisites()) {
                if (!schedule.getAllAssignments().containsKey(prerequisite)) {
                    return true;
                }
            }

            return false;
        }
    }

    class AutoScheduler {
        private ConflictDetector conflictDetector;

        public AutoScheduler(ConflictDetector conflictDetector) {
            this.conflictDetector = conflictDetector;
        }

        public Schedule generateSchedule(List<Course> courses, List<TimeSlot> preferredTimeSlots) {
            Schedule schedule = new Schedule();
            if (backtrack(schedule, new ArrayList<>(courses), preferredTimeSlots)) {
                return schedule;
            }
            return null; // No valid schedule found
        }

        private boolean backtrack(Schedule schedule, List<Course> remainingCourses, List<TimeSlot> preferredTimeSlots) {
            if (remainingCourses.isEmpty()) {
                return true; // All courses scheduled successfully
            }

            Course course = remainingCourses.remove(0);
            for (TimeSlot timeSlot : course.getAvailableTimeSlots()) {
                if (!conflictDetector.hasConflict(schedule, course, timeSlot)) {
                    schedule.assignCourse(course, timeSlot);
                    if (backtrack(schedule, remainingCourses, preferredTimeSlots)) {
                        return true;
                    }
                    schedule.removeCourse(course);
                }
            }

            remainingCourses.add(0, course); // Backtrack
            return false;
        }
    }

    class ScheduleAdjuster {
        private ConflictDetector conflictDetector;

        public ScheduleAdjuster(ConflictDetector conflictDetector) {
            this.conflictDetector = conflictDetector;
        }

        public boolean moveCourse(Schedule schedule, Course course, TimeSlot newTimeSlot) {
            if (!conflictDetector.hasConflict(schedule, course, newTimeSlot)) {
                schedule.assignCourse(course, newTimeSlot);
                return true;
            }
            return false;
        }
    }

    class ScheduleExporter {
        public void exportToPDF(Schedule schedule, String filePath) {
            // Implement PDF export logic here
            System.out.println("Exporting schedule to PDF: " + filePath);
        }

        public void exportToCalendar(Schedule schedule, String filePath) {
            // Implement calendar file export logic here
            System.out.println("Exporting schedule to calendar file: " + filePath);
        }
    }

    // Main application class
    public class CourseSchedulerApp {
        private CourseManager courseManager;
        private ConflictDetector conflictDetector;
        private AutoScheduler autoScheduler;
        private ScheduleAdjuster scheduleAdjuster;
        private ScheduleExporter scheduleExporter;

        public CourseSchedulerApp() {
            this.courseManager = new CourseManager();
            this.conflictDetector = new ConflictDetector();
            this.autoScheduler = new AutoScheduler(conflictDetector);
            this.scheduleAdjuster = new ScheduleAdjuster(conflictDetector);
            this.scheduleExporter = new ScheduleExporter();
        }

        public static void main(String[] args) {
            CourseSchedulerApp app = new CourseSchedulerApp();
            app.run();
        }

        public void run() {
            // Sample usage of the Course Scheduler
            initializeSampleData();

            List<Course> coursesToSchedule = courseManager.getCourses();
            List<TimeSlot> preferredTimeSlots = new ArrayList<>(); // Add preferred time slots here

            Schedule generatedSchedule = autoScheduler.generateSchedule(coursesToSchedule, preferredTimeSlots);

            if (generatedSchedule != null) {
                System.out.println("Schedule generated successfully!");
                printSchedule(generatedSchedule);

                // Example of manual adjustment
                Course courseToMove = coursesToSchedule.get(0);
                TimeSlot newTimeSlot = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 30));
                if (scheduleAdjuster.moveCourse(generatedSchedule, courseToMove, newTimeSlot)) {
                    System.out.println("Course moved successfully!");
                    printSchedule(generatedSchedule);
                } else {
                    System.out.println("Failed to move course due to conflicts.");
                }

                // Export the schedule
                scheduleExporter.exportToPDF(generatedSchedule, "schedule.pdf");
                scheduleExporter.exportToCalendar(generatedSchedule, "schedule.ics");
            } else {
                System.out.println("Failed to generate a valid schedule.");
            }
        }

        private void initializeSampleData() {
            // Create sample courses
            Course math101 = new Course("Math 101", 3);
            Course physics201 = new Course("Physics 201", 4);
            Course computerScience301 = new Course("Computer Science 301", 4);

            // Add prerequisites
            physics201.addPrerequisite(math101);
            computerScience301.addPrerequisite(math101);

            // Add available time slots
            math101.addAvailableTimeSlot(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 30)));
            math101.addAvailableTimeSlot(new TimeSlot(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(10, 30)));
            physics201.addAvailableTimeSlot(new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(13, 0), LocalTime.of(15, 0)));
            physics201.addAvailableTimeSlot(new TimeSlot(DayOfWeek.THURSDAY, LocalTime.of(13, 0), LocalTime.of(15, 0)));
            computerScience301.addAvailableTimeSlot(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(16, 0)));
            computerScience301.addAvailableTimeSlot(new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(12, 0)));

            // Add courses to the course manager
            courseManager.addCourse(math101);
            courseManager.addCourse(physics201);
            courseManager.addCourse(computerScience301);
        }

        private void printSchedule(Schedule schedule) {
            System.out.println("Current Schedule:");
            for (Map.Entry<Course, TimeSlot> entry : schedule.getAllAssignments().entrySet()) {
                Course course = entry.getKey();
                TimeSlot timeSlot = entry.getValue();
                System.out.printf("%s: %s %s-%s%n", course.getName(), timeSlot.getDay(),
                        timeSlot.getStartTime(), timeSlot.getEndTime());
            }
            System.out.println();
        }
    }

