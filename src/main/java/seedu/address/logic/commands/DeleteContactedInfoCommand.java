package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DELETE_CONTACTED_INFO;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.contactedinfo.ContactedInfo;
import seedu.address.model.date.BirthDate;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.reminder.ReminderList;
import seedu.address.model.tag.Tag;

/**
 * Deletes a specified contacted info from an existing person in the address book, if list is not empty.
 */
public class DeleteContactedInfoCommand extends Command {
    public static final String COMMAND_WORD = "delContacted";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a contacted information from an existing contact, "
            + "as specified by the index number used in the displayed person list. The tag will be deleted only if "
            + "the contacted information list is not empty\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_DELETE_CONTACTED_INFO + "INDEX\n"
            + "Example: " + COMMAND_WORD + " 2 "
            + PREFIX_DELETE_CONTACTED_INFO + "1";

    public static final String MESSAGE_DELETE_CONTACTED_INFO_SUCCESS = "Deleted contacted information: %1$s";
    public static final String MESSAGE_MISSING_INFO = "Index specified out of range.";
    public static final String MESSAGE_EMPTY_LIST = "Cannot delete, list is empty!";

    private final Index index;
    private final int indexToDel;

    private ContactedInfo contactedInfo;
    /**
     * Constructs an {@code DeleteContactedInfoCommand} with the given {@code Index} and {@code ContactedInfo}.
     *
     * @param index of the person in the filtered person list to add the {@code Tag}.
     * @param indexToDel  to be deleted from the {@code Person} specified by {@code index}.
     */
    public DeleteContactedInfoCommand(Index index, int indexToDel) {
        requireNonNull(index);
        requireNonNull(indexToDel);

        this.index = index;
        this.indexToDel = indexToDel - 1;
    }

    /**
     * Deletes a tag from an existing person in the address book, if the tag exists.
     *
     * @param model {@code Model} which the command should operate on.
     * @return the command result after the command execution.
     * @throws CommandException if the {@code Tag} do not exist or the {@code Index} is invalid.
     */
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());

        if (personToEdit.getContactedInfoList().isEmpty()) {
            throw new CommandException(MESSAGE_EMPTY_LIST);
        }

        if (!(personToEdit.getContactedInfoList().size() > indexToDel)) {
            throw new CommandException(MESSAGE_MISSING_INFO);
        }

        if (!(personToEdit.getContactedInfoList().size() > indexToDel)) {
            throw new CommandException(MESSAGE_MISSING_INFO);
        }

        if (indexToDel < 0) {
            throw new CommandException(MESSAGE_MISSING_INFO);
        }

        contactedInfo = personToEdit.getContactedInfoList().get(indexToDel);
        Person editedPerson = createPersonWithDeletedContactedInfo(personToEdit, this.contactedInfo);

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(MESSAGE_DELETE_CONTACTED_INFO_SUCCESS);
    }

    /**
     * Creates and returns a {@code Person} with the details of personToEdit {@code Person}
     * with tagToDelete {@code Tag} deleted.
     *
     * @param personToEdit the {@code Person} to delete the {@code ContactedInfo} from.
     * @param contactedInfoToDel the {@code ContactedInfo} to delete from {@code Person}.
     * @return the edited {@code Person} with contactedInfoToDel {@code ContactedInfo} deleted.
     */
    private static Person createPersonWithDeletedContactedInfo(Person personToEdit, ContactedInfo contactedInfoToDel) {
        assert personToEdit != null;

        Name updatedName = personToEdit.getName();
        Phone updatedPhone = personToEdit.getPhone();
        Email updatedEmail = personToEdit.getEmail();
        Address updatedAddress = personToEdit.getAddress();
        BirthDate updatedBirthDate = personToEdit.getBirthDate();
        List<ContactedInfo> updatedContactedInfo = deleteContactedInfoFromList(
                personToEdit.getContactedInfoList(), contactedInfoToDel);
        Set<Tag> updatedTags = personToEdit.getTags();
        ReminderList updatedReminders = personToEdit.getReminderList();

        return new Person(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedBirthDate,
                updatedContactedInfo, updatedTags, updatedReminders);
    }

    /**
     * Deletes a {@code ContactedInfo} from a list by creating a new {@code List} (immutable)
     * if the contacted info exists.
     *
     * @param contactedInfoArrayList the list to delete a {@code ContactedInfo} from.
     * @param contactedInfoToDel the {@code ContactedInfo} to be deleted.
     * @return an immutable list consisting of the existing {@code ContactedInfo} and {@code contactedInfoToDel}.
     */
    private static List<ContactedInfo> deleteContactedInfoFromList(
            List<ContactedInfo> contactedInfoArrayList, ContactedInfo contactedInfoToDel) {
        ArrayList<ContactedInfo> updatedList = new ArrayList<>(contactedInfoArrayList);
        updatedList.remove(contactedInfoToDel);
        return Collections.unmodifiableList(updatedList);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteContactedInfoCommand)) {
            return false;
        }

        // state check
        DeleteContactedInfoCommand e = (DeleteContactedInfoCommand) other;
        return this.index.equals(e.index) && indexToDel == e.indexToDel;
    }
}
