function getCsrfToken() {
    return document.querySelector('meta[name="_csrf"]').getAttribute('content');
}

function getCsrfHeader() {
    return document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
}

// Create Contact
function createContact(event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    
    const contact = {
        names: [{
            givenName: formData.get('firstName'),
            familyName: formData.get('lastName')
        }],
        emailAddresses: [{
            value: formData.get('email')
        }],
        phoneNumbers: [{
            value: formData.get('phone')
        }]
    };

    fetch('/contacts/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [getCsrfHeader()]: getCsrfToken()
        },
        body: JSON.stringify(contact)
    })
    .then(response => {
        if (response.ok) {
            window.location.reload();
        }
    })
    .catch(error => console.error('Error:', error));
}

// Update Contact
function updateContact(resourceName, event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    
    const contact = {
        names: [{
            givenName: formData.get('firstName'),
            familyName: formData.get('lastName')
        }],
        emailAddresses: [{
            value: formData.get('email')
        }],
        phoneNumbers: [{
            value: formData.get('phone')
        }]
    };

    fetch(`/contacts/${resourceName}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            [getCsrfHeader()]: getCsrfToken()
        },
        body: JSON.stringify(contact)
    })
    .then(response => {
        if (response.ok) {
            closeModal('editModal');
            window.location.reload();
        }
    })
    .catch(error => console.error('Error:', error));
}

function deleteContact(resourceName) {
    if (confirm('Are you sure you want to delete this contact?')) {
        // Remove 'people/' prefix if it exists
        const contactId = resourceName.replace('people/', '');
        
        fetch(`/contacts/${contactId}`, {
            method: 'DELETE',
            headers: {
                [getCsrfHeader()]: getCsrfToken()
            }
        })
        .then(response => {
            if (response.ok) {
                window.location.reload();
            } else {
                console.error('Failed to delete contact');
                throw new Error('Failed to delete contact');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Failed to delete contact. Please try again.');
        });
    }
}

// Modal Functions
function openModal(modalId) {
    document.getElementById(modalId).style.display = 'block';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

// Open Edit Modal with Contact Data
function openEditModal(contact) {
    const editForm = document.getElementById('editForm');
    editForm.firstName.value = contact.names?.[0]?.givenName || '';
    editForm.lastName.value = contact.names?.[0]?.familyName || '';
    editForm.email.value = contact.emailAddresses?.[0]?.value || '';
    editForm.phone.value = contact.phoneNumbers?.[0]?.value || '';
    editForm.resourceName.value = contact.resourceName;
    openModal('editModal');
}

function handleEdit(button) {
    const contactData = button.getAttribute('data-contact');
    const contact = JSON.parse(contactData);
    openEditModal(contact);
}

function handleEditSubmit(event) {
    event.preventDefault();
    const form = event.target;
    const resourceName = form.resourceName.value;
    
    const contact = {
        etag: '*',
        names: [{
            givenName: form.firstName.value,
            familyName: form.lastName.value,
            displayName: `${form.firstName.value} ${form.lastName.value}`
        }],
        emailAddresses: [{
            value: form.email.value
        }],
        phoneNumbers: [{
            value: form.phone.value
        }]
    };

    fetch(`/contacts/${encodeURIComponent(resourceName)}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            [getCsrfHeader()]: getCsrfToken()
        },
        body: JSON.stringify(contact)
    })
    .then(response => {
        if (response.ok) {
            alert('Contact updated successfully!');
            closeModal('editModal');
            window.location.reload();
        } else {
            return response.text().then(text => { throw new Error(text); });
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Failed to update contact. Please try again.');
    });
}

function handleDelete(button) {
    const resourceName = button.getAttribute('data-resource-name');
    deleteContact(resourceName);
}